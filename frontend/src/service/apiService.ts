// services/apiService.ts
const API_BASE_URL = 'http://127.0.0.1:8080/api/game';

// ==================== INTERFACES ====================

export interface ApiResponse<T> {
    success: boolean;
    message?: string;
    gameState?: T;
    gameOver?: boolean;
}

export interface InitGamePayload {
    gameMode: string;
    minionConfigs: MinionConfig[];
    withFreeSpawn: boolean;
}

export interface MinionConfig {
    customName: string;
    defenseFactor: number;
    strategyCode?: string;
    strategyFile?: string;
}

export interface GameState {
    turnCounter: number;
    currentPlayerName: string;
    gameMode: string;
    players: PlayerDTO[];
    board: HexTileDTO[];
    availableMinionTypes: string[];
    gameLog: string[];
}

export interface PlayerDTO {
    name: string;
    shortName: string;
    budget: number;
    ownedHexCount: number;
    minionCount: number;
    totalSpawns: number;
    canSpawn: boolean;
    isBot: boolean;
}

export interface HexTileDTO {
    row: number;
    col: number;
    occupied: boolean;
    bought: boolean;
    ownerName: string | null;
    minion: MinionDTO | null;
}

export interface MinionDTO {
    name: string;
    health: number;
    defense: number;
    ownerName: string;
}

// ==================== HELPER FUNCTIONS ====================

async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Unknown error' }));
        throw new Error(error.message || `HTTP ${response.status}`);
    }
    return response.json();
}

// ==================== API CALLS ====================

export async function initGame(payload: InitGamePayload): Promise<GameState> {
    const response = await fetch(`${API_BASE_URL}/init`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    const result = await handleResponse<ApiResponse<GameState>>(response);

    if (!result.success || !result.gameState) {
        throw new Error(result.message || 'Failed to initialize game');
    }

    return result.gameState;
}

export async function fetchGameState(): Promise<GameState> {
    const response = await fetch(`${API_BASE_URL}/state`);
    const result = await handleResponse<ApiResponse<GameState>>(response);

    if (!result.gameState) {
        throw new Error('No game state available');
    }

    return result.gameState;
}

export async function buyHex(row: number, col: number): Promise<ApiResponse<GameState>> {
    const response = await fetch(`${API_BASE_URL}/purchase-hex`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ row, col })
    });

    return await handleResponse<ApiResponse<GameState>>(response);
}

export async function spawnMinion(
    row: number,
    col: number,
    minionTypeIndex: number,
    isFreeSpawn: boolean
): Promise<GameState> {
    console.log('📤 Sending spawn request:', { row, col, minionTypeIndex, isFree: isFreeSpawn });

    const response = await fetch(`${API_BASE_URL}/spawn`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            row,
            col,
            minionTypeIndex,
            isFree: isFreeSpawn
        })
    });

    const result = await handleResponse<ApiResponse<GameState>>(response);

    if (!result.success || !result.gameState) {
        throw new Error(result.message || 'Failed to spawn minion');
    }

    return result.gameState;
}

export async function endTurn(): Promise<ApiResponse<GameState>> {
    const response = await fetch(`${API_BASE_URL}/end-turn`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    });

    return await handleResponse<ApiResponse<GameState>>(response);
}

export async function selectMode(): Promise<void> {
    await fetch(`${API_BASE_URL}/select-mode`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    });
}