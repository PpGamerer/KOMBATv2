import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import StartScreen from './src/components/StartScreen';
import ModeSelection from './src/components/ModeSelection';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<StartScreen />} />
        <Route path="/mode-selection" element={<ModeSelection />} />
      </Routes>
    </Router>
  );
}

export default App;
