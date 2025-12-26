import { useEffect, useState } from "react";

export const useFetchConfig = () => {
    const [initBudget, setInitBudget] = useState(0);
    const [maxTurns, setMaxTurns] = useState(0);

    useEffect(() => {
        fetch("/api/config") // Fetch the config from the backend
            .then((response) => response.json())
            .then((data) => {
                // Extract init_budget from config
                const budgetMatch = data.content.match(/init_budget=(\d+)/);
                if (budgetMatch) {
                    setInitBudget(parseInt(budgetMatch[1], 10)); // Set initBudget
                }

                // Extract max_turns from config
                const maxTurnsMatch = data.content.match(/max_turns=(\d+)/);
                if (maxTurnsMatch) {
                    setMaxTurns(parseInt(maxTurnsMatch[1], 10)); // Set maxTurns
                }
            })
            .catch((err) => console.error("Error fetching config:", err)); // Error handling
    }, []); // Run this effect once when the component mounts

    return { initBudget, maxTurns }; // Return both values
};
