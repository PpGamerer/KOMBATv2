// src/hook/useGameFlow.ts
import { useState } from "react";
import { useHexGrid } from "./useHexGrid";
import { useMinionSelection } from "./useMinionSelection";
import * as api from "@/service/apiService";
import { GameStateResponse } from "@/components/types"; // ✅ ตรวจสอบว่ามีบรรทัดนี้

export function useGameFlow() {
  const [currentPlayer, setCurrentPlayer] = useState(1);
  const [isFreeDrop, setIsFreeDrop] = useState(true);
  const [purchasedHex, setPurchasedHex] = useState(false);
  const [isPlacingCharacter, setIsPlacingCharacter] = useState(false);
  const [loading, setLoading] = useState(false);

  const { highlightPurchasableHexes, resetHexColors, buyHex, fetchGameState } = useHexGrid();
  const { setMessage } = useMinionSelection();

  // เปลี่ยนเทิร์น หรือให้เลือกว่าจะซื้อ Hex หรือวางมินเนี่ยน
  const askTurn = (player: number) => {
    setCurrentPlayer(player);

    setTimeout(() => {
      const wantBuy = window.confirm(
          `Player ${player}'s Turn: ต้องการซื้อ Hex หรือไม่?`
      );
      if (wantBuy) {
        setMessage(`Player ${player}: กรุณาเลือก Hex ที่ต้องการซื้อ`);
        setPurchasedHex(true);
        highlightPurchasableHexes(player);
      } else {
        askPlaceCharacter(player);
      }
    }, 300);
  };

  // ถามว่าผู้เล่นต้องการวางมินเนี่ยนหรือไม่
  const askPlaceCharacter = (player: number) => {
    const wantPlace = window.confirm(
        `Player ${player}'s Turn: ต้องวางตัวละครหรือไม่?`
    );
    if (wantPlace) {
      setMessage(`Player ${player}: กรุณาเลือกมินเนี่ยนที่ต้องการลง`);
      setIsPlacingCharacter(true);
    } else {
      finishTurn();
    }
  };

  // จบ Free Drop และเปลี่ยนไปอีกผู้เล่น
  const finishFreeDropTurn = () => {
    if (currentPlayer === 1) {
      setCurrentPlayer(2);
      setIsFreeDrop(true);
    } else {
      setIsFreeDrop(false);
      setCurrentPlayer(1);
      setTimeout(() => {
        window.alert("เริ่มเทิร์นที่ 1 ของเกม!");
        askTurn(1);
      }, 300);
    }
  };

  // เมื่อวางมินเนี่ยนเสร็จ
  const finishPlacingCharacter = () => {
    setIsPlacingCharacter(false);
    finishTurn();
  };

  // จบเทิร์นและเรียก Backend
  const finishTurn = async () => {
    try {
      setLoading(true);

      // เรียก Backend เพื่อจบเทิร์นและ execute strategies
      // ✅ แก้ไขตรงนี้: บอก TypeScript ว่าผลลัพธ์คือ GameStateResponse
      const gameState = (await api.endTurn()) as GameStateResponse;

      // รีเฟรชสถานะกระดาน
      await fetchGameState();

      // ตรวจสอบว่าเกมจบหรือยัง (ตอนนี้ TypeScript จะรู้จัก gameOver แล้ว)
      if (gameState?.gameOver) {
        const winner = gameState.winner || "Unknown";
        window.alert(`🎉 เกมจบแล้ว! ผู้ชนะคือ ${winner}!`);
        return;
      }

      // เปลี่ยนไปเทิร์นถัดไป
      const nextPlayer = currentPlayer === 1 ? 2 : 1;
      setCurrentPlayer(nextPlayer);
      setTimeout(() => askTurn(nextPlayer), 500);

    } catch (err) {
      console.error("Error ending turn:", err);
      window.alert("เกิดข้อผิดพลาดในการจบเทิร์น กรุณาลองใหม่");
    } finally {
      setLoading(false);
    }
  };

  // เปลี่ยนไปเทิร์นถัดไป (ไม่จบเทิร์นยัง)
  const switchTurn = () => {
    const nextPlayer = currentPlayer === 1 ? 2 : 1;
    setCurrentPlayer(nextPlayer);
    setTimeout(() => askTurn(nextPlayer), 500);
  };

  // Execute Strategies (สำหรับเรียกตอนจบเทิร์น)
  const executeStrategies = async () => {
    try {
      setLoading(true);
      await api.executeStrategies();
      await fetchGameState();
    } catch (err) {
      console.error("Error executing strategies:", err);
    } finally {
      setLoading(false);
    }
  };

  return {
    currentPlayer,
    setCurrentPlayer,
    isFreeDrop,
    purchasedHex,
    isPlacingCharacter,
    loading,
    askTurn,
    finishFreeDropTurn,
    finishPlacingCharacter,
    finishTurn,
    executeStrategies,
  };
}