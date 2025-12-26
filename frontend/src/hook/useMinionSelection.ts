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

  // Helper: โหลดข้อมูลเข้า Input state
  const loadMinionData = (minion: MinionType) => {
    setMinionName(minion.customName || minion.name);
    setDefense(minion.defense || 100); // Default defense 100
    setStrategy(minion.strategy || "");
  };

  // Helper: บันทึกข้อมูลปัจจุบันลง Array
  const saveCurrentData = () => {
    const updatedMinions = [...selectedMinions];
    if (updatedMinions[currentIndex]) {
      updatedMinions[currentIndex] = {
        ...updatedMinions[currentIndex],
        customName: minionName,
        defense: defense === "" ? 0 : defense,
        strategy: strategy
      };
      setSelectedMinions(updatedMinions);
      sessionStorage.setItem("selectedMinions", JSON.stringify(updatedMinions));
    }
    return updatedMinions; // Return ค่าใหม่เผื่อเอาไปใช้ต่อ
  };

  // ✅ เพิ่มฟังก์ชันนี้เพื่อให้ Component เรียกใช้ได้ (แก้ Error TS2339)
  const updateMinionData = () => {
    saveCurrentData();
  };

  const handleNext = () => {
    // 1. Validate
    if (!minionName.trim() || defense === "" || !strategy.trim()) {
      setErrorMessage("กรุณากรอกข้อมูลให้ครบถ้วน");
      return;
    }

    // 2. Save Data
    const updatedList = saveCurrentData();
    setErrorMessage("");

    // 3. Move Next
    if (currentIndex < updatedList.length - 1) {
      const nextIndex = currentIndex + 1;
      setCurrentIndex(nextIndex);
      loadMinionData(updatedList[nextIndex]);
      setIsEditingName(false);
    }  else {
    // ✅ ส่ง signal ว่าเสร็จสิ้นแล้ว
    return true; // บอกว่าถึง minion สุดท้ายแล้ว
    }
    return false;
  };

  const handlePrevious = () => {
    // Save ก่อนย้อนกลับ
    const updatedList = saveCurrentData();
    setErrorMessage("");

    if (currentIndex > 0) {
      const prevIndex = currentIndex - 1;
      setCurrentIndex(prevIndex);
      loadMinionData(updatedList[prevIndex]);
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
    updateMinionData, // ✅ Export ออกมาแล้ว
  };
}