import React from "react";
import { MinionType } from "./types";

interface PlayerInfoProps {
    player: number;
    budget: number;
    selectedMinions: MinionType[];
    onMinionClick: (e: React.MouseEvent, minion: MinionType) => void;
    isBot?: boolean;
}

const PlayerInfo: React.FC<PlayerInfoProps> = ({ player, budget, selectedMinions, onMinionClick, isBot }) => {
    if (player === 1) {
        return (
            <div className="absolute top-10 left-7 flex flex-col items-center space-y-6">
                <h2
                    className="text-white text-5xl font-bold drop-shadow-md"
                    style={{
                        WebkitTextStroke: "2px rgb(225, 171, 77)",
                        fontFamily: "'Sigmar', sans-serif",
                    }}
                >
                    Player 1 {isBot && "🤖"}
                </h2>
                <p
                    className="text-white text-2xl font-bold drop-shadow-md"
                    style={{
                        color: "rgb(255, 255, 0)",
                        WebkitTextStroke: "1px rgb(255, 213, 0)",
                    }}
                >
                    💰 Budget: <span>{budget}</span>
                </p>

                <div
                    className={`text-white text-xl mt-6 ${
                        selectedMinions.length > 3
                            ? "grid grid-cols-2 gap-8"
                            : "flex flex-col space-y-4 items-center"
                    }`}
                >
                    {selectedMinions.map((minion, index) => (
                        <div
                            key={`p1-${minion.id}-${index}`}
                            className="flex flex-col items-center"
                            style={{
                                gridColumn:
                                    selectedMinions.length > 3 &&
                                    selectedMinions.length % 2 === 1 &&
                                    index === selectedMinions.length - 1
                                        ? "span 2"
                                        : "auto",
                            }}
                        >
                            <img
                                src={minion.image}
                                alt={minion.customName || minion.name}
                                className="w-28 h-28 cursor-pointer relative z-50"
                                onClick={(e) => onMinionClick(e, minion)}
                            />
                            <p style={{
                                WebkitTextStroke: "1px rgb(2, 9, 154)",
                                fontFamily: "'Sigmar', sans-serif",
                            }}>
                                {minion.customName || minion.name}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    // Player 2
    return (
        <>
            {/* Player 2 minions */}
            <div
                className={`text-white text-xl absolute bottom-40 right-10 ${
                    selectedMinions.length > 3
                        ? "grid grid-cols-2 gap-4 justify-center"
                        : "flex flex-col space-y-4 items-center"
                }`}
            >
                {selectedMinions.map((minion, index) => (
                    <div
                        key={`p2-${minion.id}-${index}`}
                        className="flex flex-col items-center"
                        style={{
                            gridColumn:
                                selectedMinions.length > 3 &&
                                selectedMinions.length % 2 === 1 &&
                                index === selectedMinions.length - 1
                                    ? "span 2"
                                    : "auto",
                            gridRow:
                                selectedMinions.length === 5 && index === 4
                                    ? "1 / 2"
                                    : "auto",
                        }}
                    >
                        <img
                            src={minion.image}
                            alt={minion.customName || minion.name}
                            className="w-28 h-28 cursor-pointer relative z-50"
                            onClick={(e) => onMinionClick(e, minion)}
                        />
                        <p
                            style={{
                                WebkitTextStroke: "1px rgb(2, 9, 154)",
                                fontFamily: "'Sigmar', sans-serif",
                            }}
                        >
                            {minion.customName || minion.name}
                        </p>
                    </div>
                ))}
            </div>

            {/* Player 2 budget & name */}
            <div className="absolute bottom-8 right-7 flex flex-col space-y-6 items-center">
                <p
                    className="text-2xl font-bold drop-shadow-md"
                    style={{
                        color: "rgb(255, 255, 0)",
                        WebkitTextStroke: "1px rgb(255, 213, 0)",
                    }}
                >
                    💰 Budget: <span>{budget}</span>
                </p>

                <h2
                    className="text-white text-5xl font-bold drop-shadow-md"
                    style={{
                        WebkitTextStroke: "2px rgb(149, 225, 73)",
                        fontFamily: "'Sigmar', sans-serif",
                    }}
                >
                    Player 2 {isBot && "🤖"}
                </h2>
            </div>
        </>
    );
};

export default PlayerInfo;