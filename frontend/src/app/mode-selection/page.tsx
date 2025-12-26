"use client";

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function ModeSelection() {
  const router = useRouter();
  const [modeChoice, setModeChoice] = useState<number>(0);

  // ✅ Reset ทุกอย่างตอนเข้าหน้านี้
  useEffect(() => {
    // Clear sessionStorage
    sessionStorage.removeItem('selectedMinions');
    sessionStorage.removeItem('gameState');

    // Clear localStorage
    localStorage.removeItem('gameMode');

    console.log('🗑️ All game data cleared on mode selection page');
  }, []);

  const handleBack = () => {
    router.push('/');
  };

  const handleModeSelect = (mode: number) => {
    setModeChoice(mode);

    // ✅ บันทึก mode ที่เลือก
    localStorage.setItem('gameMode', mode.toString());

    if (mode === 1) console.log("✅ Selected Mode: DUEL");
    else if (mode === 2) console.log("✅ Selected Mode: SOLITAIRE");
    else if (mode === 3) console.log("✅ Selected Mode: AUTO");

    // ไปหน้าเลือก minion
    router.push('/minion-selection');
  };

  return (
      <div
          className="h-screen flex flex-col justify-center items-center bg-cover bg-center"
          style={{ backgroundImage: "url('/modee.png')" }}
      >
        <div className="absolute top-8 left-8">
          <button
              onClick={handleBack}
              className="bg-gray-800 text-white text-2xl px-5 py-2 rounded-full hover:bg-gray-900 transition-all"
          >
            Back
          </button>
        </div>

        <h1 className="text-8xl font-extrabold text-gray-700 mb-16 tracking-wide">
          Select Game Mode
        </h1>

        <button
            className="bg-blue-500 text-white text-5xl px-10 py-5 rounded-full mb-10 hover:bg-blue-700 transition-all"
            onClick={() => handleModeSelect(1)}
        >
          Duel
        </button>

        <button
            className="bg-blue-500 text-white text-5xl px-10 py-5 rounded-full mb-10 hover:bg-blue-700 transition-all"
            onClick={() => handleModeSelect(2)}
        >
          Solitaire
        </button>

        <button
            className="bg-blue-500 text-white text-5xl px-10 py-5 rounded-full hover:bg-blue-700 transition-all"
            onClick={() => handleModeSelect(3)}
        >
          Auto
        </button>
      </div>
  );
}