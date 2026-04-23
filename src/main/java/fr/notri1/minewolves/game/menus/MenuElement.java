package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;

public class MenuElement {

    private final String id;
    private final float x;
    private final float y;
    private final Component text;
    private final float scale;
    private final int backgroundColor; // ARGB
    private final Runnable onClick;

    private final EntityType entityType;
    private final PlayerSkin skin;
    private final boolean isInteractable;

    private int entityId = -1;

    private MenuElement(Builder builder) {
        this.id = builder.id;
        this.x = builder.x;
        this.y = builder.y;
        this.text = builder.text;
        this.scale = builder.scale;
        this.backgroundColor = builder.backgroundColor;
        this.onClick = builder.onClick;
        this.entityType = builder.entityType;
        this.skin = builder.skin;
        this.isInteractable = builder.isInteractable;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Component getText() {
        return text;
    }

    public float getScale() {
        return scale;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public PlayerSkin getSkin() {
        return skin;
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public boolean isClickable() {
        return onClick != null;
    }

    public static class Builder {
        private final String id;
        private float x = 0f;
        private float y = 0f;
        private Component text = null;
        private float scale = 1f;
        private int backgroundColor = 0x00000000;
        private Runnable onClick = null;

        private EntityType entityType = EntityType.TEXT_DISPLAY;
        private PlayerSkin skin = null;
        private boolean isInteractable = false;

        public Builder(String id) {
            this.id = id;
        }

        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder text(Component text) {
            this.text = text;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder backgroundColor(int argb) {
            this.backgroundColor = argb;
            return this;
        }

        public Builder onClick(Runnable onClick) {
            this.onClick = onClick;
            return this;
        }

        public Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder skin(PlayerSkin skin) {
            this.skin = skin;
            return this;
        }

        public Builder isInteractable(boolean isInteractable) {
            this.isInteractable = isInteractable;
            return this;
        }

        public MenuElement build() {
            return new MenuElement(this);
        }
    }
}