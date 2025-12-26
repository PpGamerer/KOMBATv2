import React from "react";
import { MinionType } from "@/components/types";

interface MinionSelectionProps {
  selectedMinions: MinionType[];
  onMinionClick: (e: React.MouseEvent, minion: MinionType) => void;
}

const MinionSelection: React.FC<MinionSelectionProps> = ({ selectedMinions, onMinionClick }) => {
  return (
      <div className="flex flex-col items-center">
        {selectedMinions.map((minion) => (
            <div key={minion.name} className="text-center">
              <img
                  src={minion.image}
                  alt={minion.name}
                  className="w-20 h-20 cursor-pointer"
                  onClick={(e) => onMinionClick(e, minion)}
              />
              <p className="text-white font-bold mt-2">{minion.name}</p>
            </div>
        ))}
      </div>
  );
};

export default MinionSelection;
