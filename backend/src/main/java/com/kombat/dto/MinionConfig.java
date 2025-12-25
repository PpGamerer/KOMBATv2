package com.kombat.dto;

public class MinionConfig {
    private String customName;
    private int defenseFactor;
    private String strategyFile; // เผื่อไว้สำหรับเทสต์แบบใช้ไฟล์
    private String strategyCode; // ✅ เพิ่มตัวแปรนี้สำหรับรับ Code จาก Frontend

    // Constructors
    public MinionConfig() {}

    public MinionConfig(String customName, int defenseFactor, String strategyFile, String strategyCode) {
        this.customName = customName;
        this.defenseFactor = defenseFactor;
        this.strategyFile = strategyFile;
        this.strategyCode = strategyCode;
    }

    // Getters and Setters
    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public int getDefenseFactor() {
        return defenseFactor;
    }

    public void setDefenseFactor(int defenseFactor) {
        this.defenseFactor = defenseFactor;
    }

    public String getStrategyFile() {
        return strategyFile;
    }

    public void setStrategyFile(String strategyFile) {
        this.strategyFile = strategyFile;
    }

    // ✅ เพิ่ม Getter/Setter นี้เพื่อให้ GameService เรียกใช้ได้
    public String getStrategyCode() {
        return strategyCode;
    }

    public void setStrategyCode(String strategyCode) {
        this.strategyCode = strategyCode;
    }
}