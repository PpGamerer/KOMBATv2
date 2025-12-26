import React from "react";
import { HexGrid, Layout, Hexagon, Text } from "react-hexgrid";
// import "../styles/HexGridStyles.css"; 

const HexGridComponent = () => {
    const size = { x: 8, y: 8 }; // ขนาดตาราง 8x8

    return (
        <HexGrid width="100%" height="100%" viewBox="-50 -50 100 100">
            <Layout size={{ x: 5, y: 5 }} flat={true} spacing={1.05} origin={{ x: 0, y: 0 }}>
                {[...Array(size.x)].map((_, x) =>
                    [...Array(size.y)].map((_, y) => (
                        <Hexagon key={`${x}-${y}`} q={x} r={y} s={-x - y}>
                            <Text>{`${x},${y}`}</Text>
                        </Hexagon>
                    ))
                )}
            </Layout>
        </HexGrid>
    );
};

export default HexGridComponent;
