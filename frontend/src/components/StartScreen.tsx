"use client";

import React, { useEffect, useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";

const StartScreen: React.FC = () => {
  const router = useRouter();
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  if (!isClient) return null;

  return (
      <div className="relative h-screen flex flex-col justify-center items-center bg-gradient-to-b from-blue-200 to-blue-500 overflow-hidden">
        <Image
            src="/startGame.png"
            alt="Start Screen Background"
            layout="fill"
            className="object-cover opacity-80"
            priority
        />

        <div className="mt-[-30px] flex flex-col items-center gap-6">
          <Image
              src="/logo.webp"
              alt="Game Logo"
              width={420}
              height={350}
              className="animate-bounce-slow"
          />

          <button
              onClick={() => router.push("/mode-selection")}
              className="bg-blue-500 backdrop-blur-xl text-white text-4xl font-extrabold px-10 py-8 rounded-full border-4 border-white hover:scale-105 hover:bg-white/40 transition-all shadow-2xl"
          >
            Start Game
          </button>

          <div className="px-8 py-4 bg-white/20 backdrop-blur-md text-black text-2lg font-medium rounded-xl shadow-xl max-w-2xl text-center">
            KOMBAT is a <span className="font-bold">turn-based game</span> where two players attempt to eliminate the other player's minions on the battlefield.
          </div>
        </div>
      </div>
  );
};

export default StartScreen;