"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import StartScreen from "@/components/StartScreen";
import { MinionType, HexTile } from "@/components/types";
import { PURCHASABLE_COLOR, PLAYER1_COLOR, PLAYER2_COLOR, initialHexes } from "@/components/constants";
import { useFetchConfig } from "@/hook/useFetchConfig";
import PlayerInfo from "@/components/PlayerInfo";
import * as API from "@/service/apiService";

export default function PlaceMinion() {
  const {initBudget, maxTurns} = useFetchConfig();
  const [hexes, setHexes] = useState<HexTile[]>(initialHexes);
  const [selectedMinions, setSelectedMinions] = useState<MinionType[]>([]);
  const [currentPlayer, setCurrentPlayer] = useState(1);
  const [containerSize, setContainerSize] = useState({width: 0, height: 0});
  const [hexSize, setHexSize] = useState({width: 0, height: 0});
  const router = useRouter();
  const [showStartScreen, setShowStartScreen] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [draggedMinion, setDraggedMinion] = useState<MinionType | null>(null);
  const [minionPosition, setMinionPosition] = useState({x: 0, y: 0});
  const [isFreeDrop, setIsFreeDrop] = useState(true);
  const [purchasedHex, setPurchasedHex] = useState(false);
  const [isPlacingCharacter, setIsPlacingCharacter] = useState(false);
  const [turnCounter, setTurnCounter] = useState(1);
  const [showTurnCounter, setShowTurnCounter] = useState(false);
  const [isFreeDropCompleted, setIsFreeDropCompleted] = useState(false);
  const [placedMinions, setPlacedMinions] = useState<{ minionId: number; row: number; col: number }[]>([]);
  const [purchasedHexes, setPurchasedHexes] = useState<{ row: number; col: number }[]>([]);
  const [showBuyPrompt, setShowBuyPrompt] = useState(false);
  const [showPlacePrompt, setShowPlacePrompt] = useState(false);
  const [isGameOver, setIsGameOver] = useState(false);
  const [budgets, setBudgets] = useState({player1: initBudget, player2: initBudget});
  const [gameInitialized, setGameInitialized] = useState(false);
  const [isBotPlaying, setIsBotPlaying] = useState(false);
  const [isPlayer2Bot, setIsPlayer2Bot] = useState(false);

  const clearGameData = () => {
    sessionStorage.removeItem('selectedMinions');
    sessionStorage.removeItem('gameState');
    localStorage.removeItem('gameMode');
    console.log('🗑️ Game data cleared');
  };

  useEffect(() => {
    const storedMinions = sessionStorage.getItem("selectedMinions");
    if (storedMinions) {
      const minions = JSON.parse(storedMinions);
      setSelectedMinions(minions);
    } else {
      console.error("No minions found in session");
      router.push('/minion-selection');
    }
  }, [router]);

  useEffect(() => {
    if (selectedMinions.length > 0 && !gameInitialized) {
      initializeGame(selectedMinions);
    }
  }, [selectedMinions, gameInitialized]);

  // ✅ Check and trigger bot turn
  useEffect(() => {
    if (!gameInitialized || isBotPlaying || !isPlayer2Bot) return;

    const checkAndTriggerBot = async () => {
      try {
        const gameState = await API.fetchGameState();
        if (!gameState) return;

        const currentPlayerName = gameState.currentPlayerName;

        console.log("🤖 Check bot trigger:", {
          currentPlayerName,
          currentPlayer,
          isPlayer2Bot,
          isBotPlaying,
          isFreeDrop
        });

        // ✅ Trigger bot only when it's Player 2's turn
        if (currentPlayerName?.includes("2") && !isBotPlaying) {
          console.log("✅ Triggering bot turn");
          setIsBotPlaying(true);

          setTimeout(async () => {
            await handleBotTurn();
            setIsBotPlaying(false);
          }, 2000);
        }
      } catch (error) {
        console.error("❌ Error checking bot trigger:", error);
      }
    };

    checkAndTriggerBot();
  }, [currentPlayer, gameInitialized, isBotPlaying, isPlayer2Bot]);

  const initializeGame = async (minions: MinionType[]) => {
    try {
      console.log("🎮 Initializing game with minions:", minions);

      const minionConfigs = minions.map(m => ({
        customName: m.name,
        defenseFactor: m.defense || 1,
        strategyCode: m.strategy || "",
        strategyFile: ""
      }));

      const storedMode = localStorage.getItem('gameMode');
      let gameMode = "DUEL";

      if (storedMode === "1") gameMode = "DUEL";
      else if (storedMode === "2") gameMode = "SOLITAIRE";
      else if (storedMode === "3") gameMode = "AUTO";

      console.log("🎯 Selected Game Mode:", gameMode);

      const payload: API.InitGamePayload = {
        gameMode: gameMode,
        minionConfigs: minionConfigs,
        withFreeSpawn: true
      };

      console.log("📤 Sending init payload:", payload);
      const gameState = await API.initGame(payload);
      console.log("✅ Game initialized, state:", gameState);

      // Check if Player 2 is a bot
      const player2Data = gameState.players?.[1];
      const player2IsBot = player2Data?.isBot ||
          player2Data?.name?.includes("Bot") ||
          player2Data?.shortName?.includes("B");

      setIsPlayer2Bot(player2IsBot);
      console.log("🤖 Player 2 is Bot:", player2IsBot);

      updateGameStateFromBackend(gameState);
      setGameInitialized(true);
    } catch (error) {
      console.error("❌ Failed to initialize game:", error);
      setMessage("Failed to initialize game. Please try again.");
    }
  };

  const updateGameStateFromBackend = (gameState: any) => {
    if (!gameState) return;

    console.log("📥 Updating game state from backend:", gameState);

    setTurnCounter(gameState.turnCounter || 1);

    const currentPlayerName = gameState.currentPlayerName;
    if (currentPlayerName) {
      const playerNum = currentPlayerName.includes("1") ? 1 : 2;
      setCurrentPlayer(playerNum);
      console.log("👤 Current player set to:", playerNum);
    }

    if (gameState.players && gameState.players.length >= 2) {
      setBudgets({
        player1: gameState.players[0].budget || 0,
        player2: gameState.players[1].budget || 0
      });
    }

    if (gameState.board) {
      const newHexes: HexTile[] = gameState.board.map((tile: any, index: number) => {
        let color = "rgb(249, 247, 228)";
        let player = null;

        if (tile.ownerName === "P1" || tile.ownerName === "B1") {
          color = PLAYER1_COLOR;
          player = 1;
        } else if (tile.ownerName === "P2" || tile.ownerName === "B2") {
          color = PLAYER2_COLOR;
          player = 2;
        }

        return {
          id: index + 1,
          row: tile.row,
          col: tile.col,
          player: player,
          color: color,
          occupiedBy: tile.minion ? {
            name: tile.minion.name,
            image: findMinionImage(tile.minion.name)
          } : null
        };
      });

      setHexes(newHexes);
    }
  };

  const findMinionImage = (name: string): string => {
    const minion = selectedMinions.find(m => m.name === name);
    return minion?.image || "/bearr.png";
  };

  useEffect(() => {
    const updateSize = () => {
      const screenWidth = window.innerWidth * 0.8;
      const hexWidth = Math.min(screenWidth / 10, 80);
      const hexHeight = hexWidth * 0.86;
      setContainerSize({width: hexWidth * 8 * 0.75, height: hexHeight * 8});
      setHexSize({width: hexWidth, height: hexHeight});
    };
    updateSize();
    window.addEventListener("resize", updateSize);
    return () => window.removeEventListener("resize", updateSize);
  }, []);

  // Show free drop message only for human players
  useEffect(() => {
    if (!showStartScreen && isFreeDrop && gameInitialized) {
      // Don't show message if current player is Bot
      if (currentPlayer === 2 && isPlayer2Bot) {
        return;
      }

      const timer = setTimeout(() => {
        setMessage(`Player ${currentPlayer}: ลงฟรีได้ 1 มินเนียน \nกรุณาเลือกมินเนียนที่ต้องการลง`);
      }, 500);
      return () => clearTimeout(timer);
    }
  }, [showStartScreen, isFreeDrop, currentPlayer, gameInitialized, isPlayer2Bot]);

  const highlightPurchasableHexes = (player: number) => {
    setHexes((prevHexes) =>
        prevHexes.map((hex) => {
          if (!hex.player && canPlayerPlaceHex(hex, player)) {
            return {...hex, color: PURCHASABLE_COLOR};
          }
          return hex;
        })
    );
  };

  const canPlayerPlaceHex = (hex: HexTile, player: number) => {
    return getAdjacentHexes(hex).some(
        (adjHex) =>
            hexes.find((h) => h.row === adjHex.row && h.col === adjHex.col)?.player === player
    );
  };

  const getAdjacentHexes = (hex: { row: number; col: number }) => {
    const gridRow = (hex.row * 2) + (hex.col % 2 === 0 ? 2 : 1);

    const evenRowOffsets = [
      {row: -1, col: 0}, {row: 0, col: 1}, {row: 1, col: 1},
      {row: 1, col: 0}, {row: 1, col: -1}, {row: 0, col: -1},
    ];

    const oddRowOffsets = [
      {row: -1, col: 0}, {row: -1, col: 1}, {row: 0, col: 1},
      {row: 1, col: 0}, {row: 0, col: -1}, {row: -1, col: -1},
    ];

    const offsets = gridRow % 2 === 0 ? evenRowOffsets : oddRowOffsets;

    return offsets
        .map((offset) => ({
          row: hex.row + offset.row,
          col: hex.col + offset.col,
        }))
        .filter(
            (neighbor) =>
                neighbor.row >= 0 && neighbor.row < 8 &&
                neighbor.col >= 0 && neighbor.col < 8
        );
  };

  const resetHexColors = () => {
    setHexes((prevHexes) =>
        prevHexes.map((hex) => ({
          ...hex,
          color:
              hex.player === 1 ? PLAYER1_COLOR :
                  hex.player === 2 ? PLAYER2_COLOR :
                      "rgb(255, 255, 255)",
        }))
    );
  };

  const buyHex = async (id: number) => {
    const hex = hexes.find((h) => h.id === id);
    if (!hex || hex.player !== null || hex.color !== PURCHASABLE_COLOR) return;

    try {
      const response = await API.buyHex(hex.row, hex.col);
      if (response.success) {
        updateGameStateFromBackend(response.gameState);
        setPurchasedHex(false);
        resetHexColors();
        setTimeout(() => askPlaceCharacter(currentPlayer), 500);
      }
    } catch (error) {
      console.error("Failed to buy hex:", error);
    }
  };

  const askTurn = (player: number) => {
    if (isGameOver) return;

    // Don't show prompt if it's Bot's turn
    if (player === 2 && isPlayer2Bot) {
      console.log("🤖 Bot turn, skipping prompt");
      return;
    }

    setCurrentPlayer(player);
    setTimeout(() => {
      setShowBuyPrompt(true);
    }, 300);
  };

  const askPlaceCharacter = (player: number) => {
    // Don't show prompt if it's Bot's turn
    if (player === 2 && isPlayer2Bot) {
      console.log("🤖 Bot turn, skipping place prompt");
      return;
    }

    setShowPlacePrompt(true);
  };

  const handleYesBuy = () => {
    setShowBuyPrompt(false);
    setPurchasedHex(true);
    highlightPurchasableHexes(currentPlayer);
  };

  const handleNoBuy = () => {
    setShowBuyPrompt(false);
    askPlaceCharacter(currentPlayer);
  };

  const handleYesPlace = () => {
    setShowPlacePrompt(false);
    setIsPlacingCharacter(true);
  };

  const handleNoPlace = async () => {
    setShowPlacePrompt(false);

    try {
      const response = await API.endTurn();

      if (response.gameOver) {
        setIsGameOver(true);
        return;
      }

      updateGameStateFromBackend(response.gameState);
    } catch (error) {
      console.error("Failed to end turn:", error);
    }
  };

  const handleBotTurn = async () => {
    console.log("🤖 Bot is playing...");
    setMessage("🤖 Bot กำลังคิด...");

    try {
      await new Promise(resolve => setTimeout(resolve, 1500));

      const response = await API.endTurn();
      console.log("🤖 Bot turn response:", response);

      if (response.gameOver) {
        console.log("🏁 Game over!");
        setIsGameOver(true);
        setMessage(null);
        return;
      }

      updateGameStateFromBackend(response.gameState);
      setMessage(null);

      // Check if free spawn is completed
      const logs = response.gameState?.gameLog || [];
      const freeSpawnCompleted = logs.some((log: string) =>
          log.includes("Free spawn phase completed")
      );

      if (freeSpawnCompleted) {
        console.log("✅ Free spawn completed, starting turn 1");
        setIsFreeDrop(false);
        setIsFreeDropCompleted(true);
        setShowTurnCounter(true);

        setTimeout(() => {
          askTurn(1); // Start Player 1's first turn
        }, 1000);
      }
    } catch (error) {
      console.error("❌ Bot turn failed:", error);
      setMessage(null);
    }
  };

  const finishPlacingCharacter = async () => {
    setIsPlacingCharacter(false);

    try {
      const response = await API.endTurn();

      if (response.gameOver) {
        setIsGameOver(true);
        return;
      }

      updateGameStateFromBackend(response.gameState);
    } catch (error) {
      console.error("Failed to end turn:", error);
    }
  };

  const handleHexClick = (id: number) => {
    if (isGameOver) return;

    if (isBotPlaying) {
      console.log("⏳ Bot is playing, please wait");
      return;
    }

    if (draggedMinion && isFreeDrop) {
      placeFreeDropMinion(id);
      return;
    }
    if (draggedMinion && isPlacingCharacter) {
      placeNormalDropMinion(id);
      return;
    }
    if (purchasedHex) {
      buyHex(id);
    }
  };

  const placeFreeDropMinion = async (id: number) => {
    const hex = hexes.find((h) => h.id === id);
    if (!hex || hex.occupiedBy || hex.player !== currentPlayer) {
      setMessage("❌ ไม่สามารถวางมินเนียนได้!");
      setDraggedMinion(null);
      return;
    }

    try {
      const minionIndex = selectedMinions.findIndex(m => m.name === draggedMinion?.name);
      console.log(`🎯 Player ${currentPlayer} spawning minion at (${hex.row}, ${hex.col})`);

      const gameState = await API.spawnMinion(hex.row, hex.col, minionIndex, true);
      console.log("📥 Response from backend:", gameState);

      updateGameStateFromBackend(gameState);

      setDraggedMinion(null);
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);

      const logs = gameState.gameLog || [];
      const freeSpawnCompleted = logs.some((log: string) =>
          log.includes("Free spawn phase completed")
      );

      if (freeSpawnCompleted) {
        console.log("🎮 Starting turn 1");
        setIsFreeDrop(false);
        setIsFreeDropCompleted(true);
        setShowTurnCounter(true);
        setMessage("เริ่ม turn ที่ 1 ของเกม!");

        setTimeout(() => {
          setMessage(null);
          askTurn(1);
        }, 1500);
      } else {
        // Don't show message for Bot
        const nextPlayerNum = gameState.currentPlayerName.includes("1") ? 1 : 2;
        if (nextPlayerNum === 2 && isPlayer2Bot) {
          console.log("🤖 Next is Bot free spawn");
        } else {
          setTimeout(() => {
            setMessage(`Player ${nextPlayerNum}: ลงฟรีได้ 1 มินเนียน \nกรุณาเลือกมินเนียนที่ต้องการลง`);
          }, 500);
        }
      }
    } catch (error) {
      console.error("❌ Failed to spawn minion:", error);
      setMessage(`❌ ${error instanceof Error ? error.message : 'ไม่สามารถวางมินเนียนได้'}`);
      setDraggedMinion(null);
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    }
  };

  const placeNormalDropMinion = async (id: number) => {
    const hex = hexes.find((h) => h.id === id);
    if (!hex || hex.occupiedBy || hex.player !== currentPlayer) {
      setMessage("❌ ไม่สามารถวางมินเนียนได้!");
      setDraggedMinion(null);
      return;
    }

    try {
      const minionIndex = selectedMinions.findIndex(m => m.name === draggedMinion?.name);
      const gameState = await API.spawnMinion(hex.row, hex.col, minionIndex, false);

      updateGameStateFromBackend(gameState);
      setDraggedMinion(null);
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
      finishPlacingCharacter();
    } catch (error) {
      console.error("❌ Failed to spawn minion:", error);
      setMessage(`❌ ${error instanceof Error ? error.message : 'ไม่สามารถวางมินเนียนได้'}`);
      setDraggedMinion(null);
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    }
  };

  const handleMinionClick = (e: React.MouseEvent, minion: MinionType) => {
    e.preventDefault();

    if (isBotPlaying) {
      console.log("⏳ Bot is playing, cannot drag minion");
      return;
    }

    if (!isFreeDrop && !isPlacingCharacter) return;

    setDraggedMinion({...minion});
    setMinionPosition({x: e.clientX, y: e.clientY});
    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
  };

  const handleMouseMove = (e: MouseEvent) => {
    setMinionPosition({x: e.clientX, y: e.clientY});
  };

  const handleMouseUp = (e: MouseEvent) => {
    document.removeEventListener("mousemove", handleMouseMove);
    document.removeEventListener("mouseup", handleMouseUp);
    if (!draggedMinion) return;

    const nearestHex = hexes.reduce(
        (closest, hex) => {
          const hexCenterX = hex.col * hexSize.width * 0.75 + hexSize.width / 2;
          const hexCenterY = hex.row * hexSize.height +
              (hex.col % 2 === 0 ? 0 : hexSize.height * 0.5) +
              hexSize.height / 2;
          const distance = Math.hypot(hexCenterX - e.clientX, hexCenterY - e.clientY);
          return distance < (closest.distance || Infinity)
              ? {hex, distance}
              : closest;
        },
        {hex: null as HexTile | null, distance: Infinity}
    ).hex;

    if (nearestHex && !nearestHex.occupiedBy) {
      if (isFreeDrop) {
        placeFreeDropMinion(nearestHex.id);
      } else if (isPlacingCharacter) {
        placeNormalDropMinion(nearestHex.id);
      }
    }
    setDraggedMinion(null);
  };

  const handleCancel = () => {
    const exitGame = window.confirm("ต้องการออกจากเกมหรือไม่?");
    if (exitGame) {
      clearGameData();
      router.push('/');
    }
  };

  const handleRestart = () => {
    clearGameData();
    router.push('/');
  };

  const handleNo = () => {
    clearGameData();
    router.push('/mode-selection');
  };

  if (!gameInitialized && selectedMinions.length > 0) {
    return (
        <div className="h-screen w-screen flex items-center justify-center bg-gray-900">
          <div className="text-white text-3xl">🎮 Initializing Game...</div>
        </div>
    );
  }

  return (
      <div className="relative h-screen w-screen overflow-hidden">
        {/* Only show prompts for human players */}
        {showBuyPrompt && !showStartScreen && !(currentPlayer === 2 && isPlayer2Bot) && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white p-6 rounded-lg text-center">
                <p className="text-xl mb-4 text-black">Player {currentPlayer}'s Turn: ต้องการซื้อ hex หรือไม่?</p>
                <button onClick={handleYesBuy} className="bg-green-500 text-white px-6 py-2 rounded mr-4">Yes</button>
                <button onClick={handleNoBuy} className="bg-red-500 text-white px-6 py-2 rounded">No</button>
              </div>
            </div>
        )}

        {showPlacePrompt && !(currentPlayer === 2 && isPlayer2Bot) && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white p-6 rounded-lg text-center">
                <p className="text-xl mb-4 text-black">Player {currentPlayer}'s Turn: ต้องวางตัวละครหรือไม่?</p>
                <button onClick={handleYesPlace} className="bg-green-500 text-white px-6 py-2 rounded mr-4">Yes</button>
                <button onClick={handleNoPlace} className="bg-red-500 text-white px-6 py-2 rounded">No</button>
              </div>
            </div>
        )}

        {message && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white p-6 rounded-lg text-center w-[400px]">
                <p className="text-xl mb-4 text-black">
                  {message.split("\n").map((line, index) => (
                      <span key={index}>{line}<br/></span>
                  ))}
                </p>
                <button onClick={() => setMessage(null)} className="bg-blue-500 text-white px-6 py-2 rounded">OK</button>
              </div>
            </div>
        )}

        {showStartScreen ? (
            <StartScreen/>
        ) : (
            <>
              <div className="absolute inset-0 bg-cover bg-center"
                   style={{backgroundImage: "url('/backdrop.jpg')"}}></div>

              <PlayerInfo player={1} budget={budgets.player1} selectedMinions={selectedMinions}
                          onMinionClick={handleMinionClick}/>
              <PlayerInfo player={2} budget={budgets.player2} selectedMinions={selectedMinions}
                          onMinionClick={handleMinionClick}/>

              <div className="absolute top-4 right-4 z-50">
                <button onClick={handleCancel} className="w-12 h-12 exit-button">
                  <img src="/exit.png" alt="Exit Button" className="w-full h-full object-contain"/>
                </button>
              </div>

              {showTurnCounter && isFreeDropCompleted && (
                  <div className="absolute top-4 right-16 bg-gray-900 text-white px-4 py-2 rounded-md shadow-md text-lg">
                    Turn: {turnCounter}/{maxTurns}
                  </div>
              )}

              {isGameOver && (
                  <div className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50">
                    <div className="bg-white p-8 rounded-lg text-center text-3xl font-bold text-black space-y-6">
                      Game Over <br/> Max turns reached!
                      <div className="text-xl font-bold text-black">
                        Play Again ?
                        <div className="flex justify-center space-x-6">
                          <button onClick={handleNo} className="mt-2 bg-green-500 text-white px-3 py-2 rounded-lg text-lg">Yes</button>
                          <button onClick={handleRestart} className="mt-2 bg-red-500 text-white px-4 py-2 rounded-lg text-lg">No</button>
                        </div>
                      </div>
                    </div>
                  </div>
              )}

              <div className="mt-6 relative z-10 flex flex-col items-center justify-center h-full">
                <h1 className="text-6xl font-bold text-white mb-8 mt-[-70px]"
                    style={{WebkitTextStroke: "1px rgb(21, 0, 126)", fontFamily: "'Sigmar', sans-serif"}}>
                  Player {currentPlayer}'s Turn
                </h1>

                <div className="mt-2 relative"
                     style={{width: `${containerSize.width}px`, height: `${containerSize.height}px`}}>
                  {hexes.map((hex) => {
                    const hexX = hex.col * hexSize.width * 0.75;
                    const hexY = hex.row * hexSize.height;
                    return (
                        <div key={hex.id} className="absolute mt-[-10px]"
                             style={{
                               width: `${hexSize.width}px`,
                               height: `${hexSize.height}px`,
                               left: `${hexX}px`,
                               top: `${hexY}px`,
                               transform: hex.col % 2 === 0 ? `translateY(${hexSize.height / 2}px)` : "none",
                             }}
                             onClick={() => handleHexClick(hex.id)}>
                          <div style={{
                            width: "100%", height: "100%", backgroundColor: "gray",
                            clipPath: "polygon(25% 0%, 75% 0%, 100% 50%, 75% 100%, 25% 100%, 0% 50%)",
                            display: "flex", alignItems: "center", justifyContent: "center",
                            padding: "2px", boxSizing: "border-box"
                          }}>
                            <div style={{
                              width: "100%", height: "100%", backgroundColor: hex.color,
                              clipPath: "polygon(25% 0%, 75% 0%, 100% 50%, 75% 100%, 25% 100%, 0% 50%)",
                              position: "relative"
                            }}>
                              {hex.occupiedBy && (
                                  <img src={hex.occupiedBy.image} alt={hex.occupiedBy.name}
                                       className="w-16 h-16 absolute inset-0 m-auto pointer-events-none"/>
                              )}
                            </div>
                          </div>
                        </div>
                    );
                  })}
                </div>
              </div>

              {draggedMinion && (
                  <div style={{
                    position: "absolute",
                    left: `${minionPosition.x}px`,
                    top: `${minionPosition.y}px`,
                    transform: "translate(-50%, -50%)",
                    pointerEvents: "none",
                    zIndex: 1000
                  }}>
                    <img src={draggedMinion.image} alt={draggedMinion.name} className="w-20 h-20"/>
                  </div>
              )}
            </>
        )}
      </div>
  );
}