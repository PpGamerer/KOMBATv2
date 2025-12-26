import { HexTile } from "@/components/types";

export const PLAYER1_COLOR = "rgb(225, 171, 77)";
export const PLAYER2_COLOR = "rgb(149, 225, 73)";
export const PURCHASABLE_COLOR = "rgb(189, 189, 189)"; // สีเหลือง

export const initialHexes: HexTile[] = Array.from({ length: 64 }, (_, index) => ({
  id: index + 1,
  row: Math.floor(index / 8),
  col: index % 8,
  player: null,
  color: "rgb(249, 247, 228)",
  occupiedBy: null,
}));

