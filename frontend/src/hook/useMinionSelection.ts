// src/hook/useMinionSelection.ts
import { useState, useEffect } from "react";
import { MinionType } from "@/components/types";

export function useMinionSelection() {
  const [selectedMinions, setSelectedMinions] = useState<MinionType[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);

  // Input States
  const [minionName, setMinionName] = useState("");
  const [defense, setDefense] = useState<number | "">("");
  const [strategy, setStrategy] = useState("");
  const [isEditingName, setIsEditingName] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  // Load from SessionStorage
  useEffect(() => {
    const stored = sessionStorage.getItem("selectedMinions");
    if (stored) {
      const parsedMinions: MinionType[] = JSON.parse(stored);
      setSelectedMinions(parsedMinions);
      if (parsedMinions.length > 0) {
        loadMinionData(parsedMinions[0]);
      }
    }
  }, []);

  // Sync input states with selectedMinions when currentIndex changes
  useEffect(() => {
    if (selectedMinions.length > 0 && selectedMinions[currentIndex]) {
      loadMinionData(selectedMinions[currentIndex]);
    }
  }, [currentIndex, selectedMinions]);

  // Helper: โหลดข้อมูลเข้า Input state
  const loadMinionData = (minion: MinionType) => {
    setMinionName(minion.customName || minion.name);
    setDefense(minion.defense || 1); // Default defense 1
    setStrategy(minion.strategy || "");
  };

  // Helper: บันทึกข้อมูลปัจจุบันลง Array
  const saveCurrentData = () => {
    const updatedMinions = [...selectedMinions];
    if (updatedMinions[currentIndex]) {
      updatedMinions[currentIndex] = {
        ...updatedMinions[currentIndex],
        customName: minionName.trim() || updatedMinions[currentIndex].name, // Use trimmed name
        defense: defense === "" ? 1 : defense,
        strategy: strategy.trim()
      };

      // Update state AND sessionStorage
      setSelectedMinions(updatedMinions);
      sessionStorage.setItem("selectedMinions", JSON.stringify(updatedMinions));

      console.log("💾 Saved minion data:", {
        index: currentIndex,
        name: minionName,
        defense: defense,
        strategy: strategy.substring(0, 50) + "..."
      });
    }
    return updatedMinions;
  };

  const updateMinionData = () => {
    saveCurrentData();
  };

  const handleNext = () => {
    // 1. Validate
    if (!minionName.trim() || defense === "" || !strategy.trim()) {
      setErrorMessage("กรุณากรอกข้อมูลให้ครบถ้วน");
      return false;
    }

    // 2. Save Data
    const updatedList = saveCurrentData();
    setErrorMessage("");

    // 3. Move Next
    if (currentIndex < updatedList.length - 1) {
      const nextIndex = currentIndex + 1;
      setCurrentIndex(nextIndex);
      // Don't load here - useEffect will handle it
      setIsEditingName(false);
      return false;
    } else {
      // ส่ง signal ว่าเสร็จสิ้นแล้ว
      return true; // บอกว่าถึง minion สุดท้ายแล้ว
    }
  };

  const handlePrevious = () => {
    // Save ก่อนย้อนกลับ
    const updatedList = saveCurrentData();
    setErrorMessage("");

    if (currentIndex > 0) {
      const prevIndex = currentIndex - 1;
      setCurrentIndex(prevIndex);
      // Don't load here - useEffect will handle it
      setIsEditingName(false);
    }
  };

  return {
    selectedMinions,
    currentIndex,
    minionName,
    defense,
    strategy,
    isEditingName,
    errorMessage,
    message,
    setMinionName,
    setDefense,
    setStrategy,
    setIsEditingName,
    setMessage,
    handleNext,
    handlePrevious,
    updateMinionData,
  };
}