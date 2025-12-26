// src/hook/useHexGrid.ts
import { useState, useEffect } from "react";
import { initialHexes, PLAYER1_COLOR, PLAYER2_COLOR } from "@/components/constants";
import { HexTile } from "@/components/types";
import * as api from "@/service/apiService"; // Import API

export function useHexGrid() {
  const [hexes, setHexes] = useState<HexTile[]>(initialHexes);

  // ดึงข้อมูล State ล่าสุดจาก Backend
  const fetchGameState = async () => {
    try {
      const data = await api.fetchGameState();
      // Map ข้อมูลจาก Backend (DTO) ให้เข้ากับ Frontend Type
      // สมมติ Backend ส่งกลับมาเป็นโครงสร้างที่ตรงกัน หรือต้องแปลงนิดหน่อยตรงนี้
      if (data.hexes) {
        setHexes(data.hexes);
      }
    } catch (error) {
      console.error("Error fetching game state:", error);
    }
  };

  // โหลดข้อมูลครั้งแรก
  useEffect(() => {
    fetchGameState();
  }, []);

  const highlightPurchasableHexes = (player: number) => {
    // Logic เดิม หรือจะรอ Backend ส่ง purchasable hexes มาก็ได้
    // เบื้องต้นอาจจะปล่อยว่างไว้ก่อนถ้าย้าย logic ไป backend หมดแล้ว
    // หรือถ้ายังใช้ Logic เดิมในการคำนวณสี ก็ใส่กลับมาได้ครับ
  };

  const resetHexColors = () => {
    // Logic คืนค่าสีเดิม
    fetchGameState(); // ง่ายสุดคือดึงค่าล่าสุดจาก Server
  };

  const buyHex = async (id: number, row: number, col: number, playerIndex: number) => {
    try {
      await api.buyHex(row, col, playerIndex);
      await fetchGameState(); // อัปเดตกระดานทันทีหลังซื้อ
    } catch (error) {
      console.error("Buy hex failed:", error);
      alert("ไม่สามารถซื้อ Hex ได้ (เงินไม่พอ หรือ ไม่ได้อยู่ติดกัน)");
    }
  };

  return {
    hexes,
    setHexes,
    highlightPurchasableHexes,
    resetHexColors,
    buyHex,          // ✅ เพิ่มส่งออกตรงนี้
    fetchGameState,  // ✅ เพิ่มส่งออกตรงนี้
  };
}