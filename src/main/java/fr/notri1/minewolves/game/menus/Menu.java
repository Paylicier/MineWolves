package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.BlockChangePacket;
import net.minestom.server.network.packet.server.play.CameraPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static fr.notri1.minewolves.MineWolves.config;
import static fr.notri1.minewolves.MineWolves.instanceContainer;

/**
 * Abstract base class for packet-based menus.
 * <p>
 * When opened, spawns an invisible armor stand high in the sky and
 * makes the player spectate it. Menu elements are Text Display entities
 * visible only to the target player (via Minestom's auto-viewable system).
 * <p>
 * Subclasses define the menu content by implementing {@link #buildElements(Player)}.
 */
public abstract class Menu {

    /**
     * Distance (in blocks) from the camera at which text displays are projected
     */
    private static final float PROJECTION_DISTANCE = 4.0f;

    /**
     * Horizontal spread factor — approximates tan(fov/2) * aspectRatio for default FOV 70°
     */
    private static final float HORIZONTAL_FACTOR = 0.75f;

    /**
     * Vertical spread factor — approximates tan(fov/2) for default FOV 70°
     */
    private static final float VERTICAL_FACTOR = 0.48f;

    // Active sessions per player
    private static final Map<UUID, MenuSession> activeSessions = new ConcurrentHashMap<>();

    public List<MenuElement> elements;

    /**
     * Checks if the given player has any menu open.
     */
    public static boolean hasMenuOpen(Player player) {
        return activeSessions.containsKey(player.getUuid());
    }

    /**
     * Gets the active menu session for a player, if any.
     */
    public static MenuSession getSession(Player player) {
        return activeSessions.get(player.getUuid());
    }

    /**
     * Statically closes any menu open for the given player.
     */
    public static void closeStatic(Player player) {
        MenuSession session = activeSessions.remove(player.getUuid());
        if (session == null) return;

        // Remove all text display entities
        for (Entity entity : session.displayEntities) {
            entity.remove();
        }

        // Remove client-side light blocks
        removeLightBlocks(player, session.lightPositions);

        // Return camera control to the player
        player.sendPacket(new CameraPacket(player.getEntityId()));

        // update player inv
        player.getInventory().update();

        // Remove the camera armor stand from the world
        session.cameraEntity.remove();

        // Teleport the player back to their original position
//        player.teleport(session.originalPos);
    }

    /**
     * Removes client-side light blocks by sending air block changes.
     */
    private static void removeLightBlocks(Player player, List<Vec> positions) {
        for (Vec pos : positions) {
            player.sendPacket(new BlockChangePacket(pos, Block.AIR));
        }
    }

    /**
     * Defines the elements to display in this menu.
     * Called when the menu is opened for a player.
     *
     * @param player the player opening the menu
     * @return list of menu elements with relative screen coordinates
     */
    protected abstract List<MenuElement> buildElements(Player player);

    /**
     * Opens this menu for the given player.
     * If the player already has a menu open, it is closed first.
     */
    public void open(Player player) {
        // Close any existing menu
        if (activeSessions.containsKey(player.getUuid())) {
            closeStatic(player);
        }

        // Save original position for restoring later
        Pos originalPos = player.getPosition();

        // Spawn invisible armor stand as camera anchor
        Pos cameraPos = new Pos(config.getGame().getMenuLocation().get(0), config.getGame().getMenuLocation().get(1), config.getGame().getMenuLocation().get(2), config.getGame().getMenuLocation().get(3), config.getGame().getMenuLocation().get(4));
        Entity cameraEntity = new Entity(EntityType.ARMOR_STAND);
        cameraEntity.setInvisible(true);
        cameraEntity.setNoGravity(true);
        cameraEntity.setAutoViewable(false);
        cameraEntity.setInstance(instanceContainer, cameraPos);
        cameraEntity.addViewer(player);

        // Make the player spectate the armor stand
        player.sendPacket(new CameraPacket(cameraEntity.getEntityId()));

        // Set slot to middle
        player.setHeldItemSlot((byte) 4);
        List<ItemStack> protocolItems = Collections.nCopies(46, ItemStack.of(Material.PAPER).withItemModel("minecraft:air").withCustomName(Component.text(" ")));

        WindowItemsPacket hideHandPacket = new WindowItemsPacket(
                (byte) 0,
                0,
                protocolItems,
                ItemStack.AIR
        );

        player.sendPacket(hideHandPacket);

        // Place client-side light blocks around the camera to illuminate the menu
        List<Vec> lightPositions = placeLightBlocks(player, cameraPos);

        // Build and spawn menu elements
        List<MenuElement> elements = buildElements(player);
        this.elements = elements;
        List<Entity> displayEntities = new ArrayList<>();

        for (MenuElement element : elements) {
            // Calculate world position from relative screen coords
            Vec offset = screenToWorldOffset(element.getX(), element.getY(), cameraPos.yaw(), cameraPos.pitch());
            Pos elementPos = cameraPos.add(offset.x(), offset.y() + getArmorStandEyeHeight(), offset.z());

            // Spawn a real Text Display entity, visible only to this player
            Entity textDisplay = null;

            if (element.getItem() != null) {
                textDisplay = createItemDisplay(element, cameraPos.yaw() - 90, cameraPos.pitch());
            } else {
                textDisplay = createTextDisplay(element, cameraPos.yaw() - 90, cameraPos.pitch());
            }

            textDisplay.setAutoViewable(false);
            textDisplay.setInstance(instanceContainer, elementPos);
            textDisplay.addViewer(player);

            element.setEntityId(textDisplay.getEntityId());
            displayEntities.add(textDisplay);
        }

        // Store the session
        MenuSession session = new MenuSession(this, cameraEntity, elements, displayEntities, originalPos, lightPositions);
        activeSessions.put(player.getUuid(), session);
    }

    /**
     * Closes this menu for the given player.
     */
    public void close(Player player) {
        closeStatic(player);
    }

    // ---- Entity creation helper ----

    /**
     * Updates a specific element's text by modifying the entity's metadata directly.
     */
    public void updateElement(Player player, String elementId, Component newText) {
        MenuSession session = activeSessions.get(player.getUuid());
        if (session == null) return;

        for (int i = 0; i < session.elements.size(); i++) {
            MenuElement element = session.elements.get(i);
            if (element.getId().equals(elementId)) {
                Entity entity = session.displayEntities.get(i);
                TextDisplayMeta meta = (TextDisplayMeta) entity.getEntityMeta();
                meta.setText(newText);
                break;
            }
        }
    }

    public void updateElementScale(Player player, String elementId, float scaleMultiplier, int interpolationDuration) {
        MenuSession session = activeSessions.get(player.getUuid());
        if (session == null) return;

        for (int i = 0; i < session.elements.size(); i++) {
            MenuElement element = session.elements.get(i);
            if (element.getId().equals(elementId)) {
                Entity entity = session.displayEntities.get(i);
                AbstractDisplayMeta  meta;

                if (entity.getEntityType() == EntityType.TEXT_DISPLAY) {
                    meta = (TextDisplayMeta) entity.getEntityMeta();
                } else if (entity.getEntityType() == EntityType.ITEM_DISPLAY) {
                    meta = (ItemDisplayMeta) entity.getEntityMeta();
                } else {
                    return; // Unsupported entity type
                }

                meta.setTransformationInterpolationDuration(interpolationDuration);
                meta.setTransformationInterpolationStartDelta(0);
                float s = element.getScale() * scaleMultiplier;
                meta.setScale(new Vec(s, s, s));
                break;
            }
        }
    }

    /**
     * Creates a Text Display entity with the given element's properties.
     * Applies a leftRotation quaternion so the text faces toward the camera.
     */
    private Entity createTextDisplay(MenuElement element, float cameraYaw, float cameraPitch) {
        Entity entity = new Entity(EntityType.TEXT_DISPLAY);
        entity.setNoGravity(true);

        TextDisplayMeta meta = (TextDisplayMeta) entity.getEntityMeta();
        meta.setText(element.getText());
        meta.setBackgroundColor(element.getBackgroundColor());
        meta.setLineWidth(400);
        meta.setTextOpacity((byte) -1); // fully opaque
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        meta.setScale(new Vec(element.getScale(), element.getScale(), element.getScale()));

        double halfYaw = Math.toRadians(cameraYaw + 180.0) / 2.0;
        double halfPitch = Math.toRadians(cameraPitch) / 2.0;

        double yW = Math.cos(halfYaw);
        double yY = Math.sin(halfYaw);

        double pW = Math.cos(halfPitch);
        double pX = Math.sin(halfPitch);

        float qx = (float) (yW * pX);
        float qy = (float) (yY * pW);
        float qz = (float) (-yY * pX);
        float qw = (float) (yW * pW);

        meta.setLeftRotation(new float[]{qx, qy, qz, qw});

        return entity;
    }

    /**
     * Creates an item display entity.
     */
    private Entity createItemDisplay(MenuElement element, float cameraYaw, float cameraPitch) {
        Entity entity = new Entity(EntityType.ITEM_DISPLAY);
        entity.setNoGravity(true);

        ItemDisplayMeta meta = (ItemDisplayMeta) entity.getEntityMeta();
        meta.setItemStack(element.getItem());
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        meta.setScale(new Vec(element.getScale(), element.getScale(), element.getScale()));

        double halfYaw = Math.toRadians(cameraYaw + 180.0) / 2.0;
        double halfPitch = Math.toRadians(cameraPitch) / 2.0;

        double yW = Math.cos(halfYaw);
        double yY = Math.sin(halfYaw);

        double pW = Math.cos(halfPitch);
        double pX = Math.sin(halfPitch);

        float qx = (float) (yW * pX);
        float qy = (float) (yY * pW);
        float qz = (float) (-yY * pX);
        float qw = (float) (yW * pW);

        meta.setLeftRotation(new float[]{qx, qy, qz, qw});

        return entity;
    }

    /**
     * Places client-side light blocks (level 15) in a grid around the camera
     * so the menu area is fully lit. Returns the positions for later cleanup.
     */
    private List<Vec> placeLightBlocks(Player player, Pos cameraPos) {
        Block lightBlock = Block.LIGHT.withProperty("level", "15");
        List<Vec> positions = new ArrayList<>();

        // Place a 3x3x3 grid of light blocks centered on the camera
        int cx = (int) Math.floor(cameraPos.x());
        int cy = (int) Math.floor(cameraPos.y());
        int cz = (int) Math.floor(cameraPos.z());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -1; dz <= (int) (PROJECTION_DISTANCE + 1); dz++) {
                    Vec pos = new Vec(cx + dx * 3, cy + dy * 2, cz + dz * 3);
                    positions.add(pos);
                    player.sendPacket(new BlockChangePacket(pos, lightBlock));
                }
            }
        }
        return positions;
    }

    /**
     * Converts relative screen coordinates (-1 to 1) to a 3D offset
     * from the camera position, rotated to match the camera's yaw and pitch.
     *
     * @param screenX horizontal position: -1 = left, 0 = center, 1 = right
     * @param screenY vertical position: -1 = bottom, 0 = center, 1 = top
     * @param yaw     the camera yaw in degrees
     * @param pitch   the camera pitch in degrees
     * @return the world offset vector from the camera
     */
    private Vec screenToWorldOffset(float screenX, float screenY, float yaw, float pitch) {
        double localRight = screenX * PROJECTION_DISTANCE * HORIZONTAL_FACTOR;
        double localUp = screenY * PROJECTION_DISTANCE * VERTICAL_FACTOR;
        double localForward = PROJECTION_DISTANCE;

        // Minecraft: yaw 0 = south (+Z), 90 = west (-X), 180 = north (-Z), 270 = east (+X)
        // pitch: 0 = horizontal, +90 = down, -90 = up
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);

        // Forward vector (standard MC look direction)
        // yaw 0 = south (+Z), 90 = west (-X); pitch 0 = horizontal, +90 = down
        double forwardX = -sinYaw * cosPitch;
        double forwardY = -sinPitch;
        double forwardZ = cosYaw * cosPitch;

        // Right vector (perpendicular to forward on horizontal plane, pointing left-to-right on screen)
        double rightX = cosYaw;
        double rightY = 0;
        double rightZ = sinYaw;

        // Up vector (cross: right × forward, then normalized — but for orthogonal basis, just cross)
        double upX = rightY * forwardZ - rightZ * forwardY;
        double upY = rightZ * forwardX - rightX * forwardZ;
        double upZ = rightX * forwardY - rightY * forwardX;

        // Combine: worldOffset = right * localRight + up * localUp + forward * localForward
        double worldX = rightX * localRight + upX * localUp + forwardX * localForward;
        double worldY = rightY * localRight + upY * localUp + forwardY * localForward;
        double worldZ = rightZ * localRight + upZ * localUp + forwardZ * localForward;

        return new Vec(worldX, worldY, worldZ);
    }

    /**
     * Armor stand eye height offset.
     * The camera is positioned at the armor stand's eye level.
     */
    private double getArmorStandEyeHeight() {
        return 1.7;
    }

    // ---- Inner class for active menu session ----

    public static class MenuSession {
        private final Menu menu;
        private final Entity cameraEntity;
        private final List<MenuElement> elements;
        private final List<Entity> displayEntities;
        private final Pos originalPos;
        private final List<Vec> lightPositions;

        public MenuSession(Menu menu, Entity cameraEntity, List<MenuElement> elements, List<Entity> displayEntities, Pos originalPos, List<Vec> lightPositions) {
            this.menu = menu;
            this.cameraEntity = cameraEntity;
            this.elements = elements;
            this.displayEntities = displayEntities;
            this.originalPos = originalPos;
            this.lightPositions = lightPositions;
        }

        public Menu getMenu() {
            return menu;
        }

        public Entity getCameraEntity() {
            return cameraEntity;
        }

        public List<MenuElement> getElements() {
            return elements;
        }

        public List<Entity> getDisplayEntities() {
            return displayEntities;
        }

        public Pos getOriginalPos() {
            return originalPos;
        }

        /**
         * Finds the closest clickable element to the given screen coordinates.
         *
         * @param lookX     normalized screen X (-1 to 1)
         * @param lookY     normalized screen Y (-1 to 1)
         * @param threshold maximum distance to consider a hit
         * @return the closest element, or null if none is within threshold
         */
        public MenuElement getElementAt(float lookX, float lookY, float threshold) {
            MenuElement closest = null;
            float closestDist = threshold;

            for (MenuElement el : elements) {
                if (!el.isClickable()) continue;
                float dx = el.getX() - lookX;
                float dy = el.getY() - lookY;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = el;
                }
            }
            return closest;
        }
    }
}






