package com.m1zark.pixeledit.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Log {
    private String purchaseInfo;
    private String timeStamp;
    private UUID owner;
    private int logID;

    public Log(int id, String info, String timestamp, UUID owner) {
        this.logID = id;
        this.purchaseInfo = info;
        this.owner = owner;
        this.timeStamp = timestamp;
    }

    public String getPurchaseInfo() {
        String info = this.purchaseInfo;
        info = StringUtils.substringBetween(info, "{", "}");

        String[] keyValuePairs = info.split(",");
        Map<String,String> infoMap = new HashMap<>();

        for(String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            infoMap.put(entry[0].trim(), entry[1].trim());
        }

        StringBuilder formatInfo = new StringBuilder();
        infoMap.forEach((k,v) -> formatInfo.append("&7"+k+": "+v + "\n") );

        return formatInfo.toString();
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public UUID getOwner() { return this.owner; }

    public String getOwnerName() {
       return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(this.owner).get().getName();
    }

    public int getLogID() { return this.logID; }
}
