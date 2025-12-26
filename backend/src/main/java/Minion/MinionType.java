package Minion;

import Parser.AST.Statement;

import java.util.List;

public class MinionType {
    private final String baseType; // 1.Cora 2.Connie 3.Charlotte 4. Cody 5. Crystal
    private String customName; // Player-defined name
    private int defenseFactor;
    private List<Statement> strategy;

    public MinionType(String baseType,String customName, int defenseFactor, List<Statement> strategy) {
        this.baseType = baseType;
        this.customName = (customName == null || customName.isEmpty()) ? baseType : customName;
        this.defenseFactor = defenseFactor;
        this.strategy = strategy;
    }

    public String getBaseType() {
        return baseType;
    }

    public String getCustomName() {
        return customName != null ? customName : baseType;
    }

    public void setCustomName(String customName) {
        if (customName == null || customName.isEmpty()) {
            this.customName = baseType;  // Default to baseType
        } else {
            this.customName = customName;
        }
    }

    public void setDefenseFactor(int defenseFactor) {
        this.defenseFactor = defenseFactor;
    }

    public int getDefenseFactor() {
        return defenseFactor;
    }

    public List<Statement> getStrategy() {
        return strategy;
    }

    public void setStrategy(List<Statement> strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return getCustomName() + " (Base: " + baseType + ", Defense: " + defenseFactor + ")";
    }
}
