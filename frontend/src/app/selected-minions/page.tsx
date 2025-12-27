"use client";
import React from "react";
import { useMinionSelection } from "@/hook/useMinionSelection";
import { useRouter } from 'next/navigation';
import { getDefaultStrategy } from "@/utils/defaultStrategies"; // Import the helper

export default function SelectedMinions() {
  const {
    selectedMinions,
    currentIndex,
    isEditingName,
    minionName,
    defense,
    strategy,
    errorMessage,
    setMinionName,
    setDefense,
    setStrategy,
    setIsEditingName,
    handleNext,
    handlePrevious,
    updateMinionData
  } = useMinionSelection();
  const router = useRouter();

  // Get the current minion's original name for placeholder
  const currentMinion = selectedMinions[currentIndex];
  const placeholderStrategy = currentMinion ? getDefaultStrategy(currentMinion.name) : "";

  const handleBack = () => {
    router.push('/minion-selection');
  };

  // Save name when finishing edit
  const handleNameBlur = () => {
    setIsEditingName(false);
    updateMinionData(); // Save immediately after editing
    console.log("💾 Minion name saved:", minionName);
  };

  // Save name when pressing Enter
  const handleNameKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      setIsEditingName(false);
      updateMinionData(); // Save immediately
      console.log("💾 Minion name saved (Enter):", minionName);
    }
  };

  const handleStartGame = () => {
    // Validate ก่อน
    if (!minionName.trim() || defense === "" || !strategy.trim()) {
      return; // handleNext จะแสดง error message
    }

    // Save ข้อมูล minion สุดท้าย
    updateMinionData();

    // Navigate ไปหน้าเกม
    router.push('/game-screen');
  };

  const handleNextClick = () => {
    if (currentIndex < selectedMinions.length - 1) {
      handleNext(); // ถ้ายังไม่ใช่ minion สุดท้าย
    } else {
      handleStartGame(); // ถ้าเป็น minion สุดท้าย
    }
  };

  // Function to use default strategy
  const useDefaultStrategy = () => {
    if (placeholderStrategy) {
      setStrategy(placeholderStrategy);
      console.log("✅ Default strategy loaded for:", currentMinion.name);
    }
  };

  return (
      <div className="h-screen flex flex-col items-center bg-cover bg-center relative" style={{ backgroundImage: "url('/selectedd.png')" }}>
        <h2 className="mt-16 text-4xl font-semibold text-black">
          Minion {currentIndex + 1} of {selectedMinions.length}
        </h2>
        <button
            onClick={handleBack}
            className="absolute top-4 left-4 bg-gray-800 text-white text-2xl px-5 py-2 rounded-full hover:bg-gray-900 transition-all"
        >
          Back
        </button>

        <div className="flex justify-between items-center w-3/4 bg-opacity-50 p-8 rounded-2xl mt-10">
          <div className="w-[28rem] text-center bg-blue-300 bg-opacity-70 p-6 rounded-3xl mr-6 h-auto">
            <img src={selectedMinions[currentIndex]?.image} alt={minionName} className="w-60 h-60 mx-auto mb-6" />
            {!isEditingName ? (
                <h2
                    className="text-5xl font-bold text-white mb-4 cursor-pointer"
                    onClick={() => setIsEditingName(true)}
                >
                  {minionName}
                </h2>
            ) : (
                <input
                    type="text"
                    value={minionName}
                    onChange={(e) => setMinionName(e.target.value)}
                    onBlur={handleNameBlur}
                    onKeyDown={handleNameKeyDown}
                    autoFocus
                    className="text-2xl font-bold text-center text-gray-800 p-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            )}
            <button
                onClick={() => setIsEditingName(true)}
                className="mt-2 bg-gray-700 text-white px-6 py-2 rounded-full hover:bg-gray-900 transition-all"
            >
              Change Minion Name...
            </button>
          </div>

          <div className="flex-1 bg-gray-200 p-10 rounded-3xl shadow-inner">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-3xl font-bold text-gray-800 text-left">Strategy</h2>
              {placeholderStrategy && !strategy && (
                  <button
                      onClick={useDefaultStrategy}
                      className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-all text-sm"
                  >
                    Use Default Strategy
                  </button>
              )}
            </div>
            <textarea
                value={strategy}
                onChange={(e) => setStrategy(e.target.value)}
                placeholder={placeholderStrategy || "Enter your custom strategy here..."}
                rows={5}
                className="w-full p-3 bg-white text-gray-900 border-2 border-blue-300 rounded-md focus:outline-none focus:ring-4 focus:ring-blue-500 font-mono text-sm"
            />
            <label className="block mt-4">
              <span className="text-2xl font-bold text-gray-800">Defense</span>
              <input
                  type="number"
                  value={defense}
                  onChange={(e) => setDefense(e.target.value === "" ? "" : Number(e.target.value))}
                  className="w-full p-3 bg-white text-gray-900 border-2 border-blue-300 rounded-md mt-2 focus:outline-none focus:ring-4 focus:ring-blue-500"
              />
            </label>
          </div>
        </div>

        {errorMessage && <p className="text-red-500 text-lg mt-4 font-semibold">{errorMessage}</p>}

        <div className="absolute bottom-8 right-8 flex space-x-4">
          <button
              className="bg-blue-500 text-white text-2xl px-6 py-2 rounded-full shadow-lg hover:bg-blue-600 transition-all"
              onClick={handlePrevious}
          >
            Previous
          </button>
          <button
              className="text-white text-2xl px-10 py-4 rounded-full shadow-lg transition-all bg-yellow-600 hover:bg-yellow-700"
              onClick={handleNextClick}
          >
            {currentIndex < selectedMinions.length - 1 ? "Next" : "Start"}
          </button>
        </div>
      </div>
  );
}