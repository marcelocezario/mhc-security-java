package br.dev.mhc.security.context;

import java.util.UUID;

public class AuthContext {

    private static final ThreadLocal<UUID> tokenUuidHolder = new ThreadLocal<>();

    public static UUID getTokenUuid() {
        return tokenUuidHolder.get();
    }

    public static void setTokenUuid(UUID uuid) {
        tokenUuidHolder.set(uuid);
    }

    public static void clear() {
        tokenUuidHolder.remove();
    }
}
