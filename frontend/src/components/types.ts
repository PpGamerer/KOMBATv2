export interface MinionType {
    name: string;
    image: string;
    defense?: number;
    strategy?: string;
    id: number;
    owner?: string; // Add owner field
    health?: number; // Add health field
    customName?: string; // ชื่อที่ผู้เล่นตั้งเอง
}

export interface HexTile {
    id: number;
    row: number;
    col: number;
    player: number | null;
    color: string;
    occupiedBy: {
        name: string;
        image: string;
        health?: number;  // Add health field
        defense?: number; // Add defense field
    } | null;
}

// Add GameState types
export interface GameStateResponse {
    status: string;
    gameState?: GameState;
    message?: string;
    gameOver?: boolean; // Add gameOver flag
    winner?: string;
}

export interface GameState {
    board: BoardTile[][];
    players: PlayerState[];
    currentPlayer: string;
    minionTypes: MinionTypeInfo[];
    config: GameConfig;
}

export interface BoardTile {
    row: number;
    col: number;
    occupied: boolean;
    owner: string | null;
    minion?: {
        name: string;
        health: number;
        defense: number;
        owner: string;
    };
}

export interface PlayerState {
    name: string;
    budget: number;
    minionCount: number;
    hexCount: number;
}

export interface MinionTypeInfo {
    index: number;
    name: string;
    baseType: string;
    defenseFactor: number;
}

export interface GameConfig {
    spawnCost: number;
    hexPurchaseCost: number;
    maxSpawns: number;
    maxBudget: number;
}