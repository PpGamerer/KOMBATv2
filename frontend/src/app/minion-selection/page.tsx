"use client";

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';

const minions = [
  { id: 1, name: 'Cora', hp: 100, image: '/bearr.png', width: 150, height: 150 },
  { id: 2, name: 'Connie', hp: 100, image: '/penguinn.png', width: 160, height: 160 },
  { id: 3, name: 'Charlotte', hp: 100, image: '/foxx.png', width: 170, height: 170 },
  { id: 4, name: 'Cody', hp: 100, image: '/seal.webp', width: 170, height: 170 },
  { id: 5, name: 'Crystal', hp: 100, image: '/egle.webp', width: 170, height: 170 },
];

export default function MinionSelection() {
  const router = useRouter();
  const [selectedMinions, setSelectedMinions] = useState<number[]>([]);

  const handleSelect = (id: number) => {
    setSelectedMinions((prev) =>
        prev.includes(id) ? prev.filter((charId) => charId !== id) : [...prev, id]
    );
  };

  const handleNext = () => {
    if (selectedMinions.length === 0) {
      alert('Please select at least one minion!');
      return;
    }

    // แค่เก็บข้อมูลไว้ใน sessionStorage
    const selected = minions.filter((char) => selectedMinions.includes(char.id));
    sessionStorage.setItem('selectedMinions', JSON.stringify(selected));

    // ไปหน้าตั้งค่า minion
    router.push('/selected-minions');
  };

  const handleBack = () => {
    sessionStorage.removeItem('selectedMinions');
    console.log('🗑️ Cleared selected minions');
    router.push('/mode-selection');
  };

  return (
      <div
          className="h-screen flex flex-col bg-cover bg-center relative px-6 py-10"
          style={{ backgroundImage: "url('/MinionSelection.png')" }}
      >
        <button
            onClick={handleBack}
            className="absolute top-4 left-4 bg-gray-800 text-white text-2xl px-5 py-2 rounded-full hover:bg-gray-900 transition-all"
        >
          Back
        </button>

        <h1 className="text-7xl font-bold text-gray-800 text-center mt-8 mb-16 tracking-widest">
          Minion Selection
        </h1>

        <div className="flex justify-center items-center w-full px-4 gap-4">
          {minions.map((char) => (
              <div
                  key={char.id}
                  className="text-center bg-white bg-opacity-70 p-6 rounded-lg shadow-md w-1/4 mx-4"
              >
                <div className="w-48 h-48 mx-auto mb-4 flex justify-center items-center">
                  <img
                      src={char.image}
                      alt={char.name}
                      width={char.width}
                      height={char.height}
                      className="rounded-md"
                  />
                </div>

                <h2 className="text-3xl font-bold text-black mb-2">{char.name}</h2>
                <div className="text-lg text-gray-700">
                  <p>💗HP: {char.hp}</p>
                </div>

                <button
                    onClick={() => handleSelect(char.id)}
                    className={`mt-4 px-8 py-2 rounded-full ${
                        selectedMinions.includes(char.id) ? 'bg-green-500' : 'bg-gray-500'
                    } text-white`}
                >
                  {selectedMinions.includes(char.id) ? 'Selected' : 'Select'}
                </button>
              </div>
          ))}
        </div>

        <button
            onClick={handleNext}
            disabled={selectedMinions.length === 0}
            className={`absolute bottom-6 right-6 text-white text-2xl px-5 py-3 rounded-full transition-all ${
                selectedMinions.length === 0
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 hover:bg-blue-800'
            }`}
        >
          Next
        </button>
      </div>
  );
}