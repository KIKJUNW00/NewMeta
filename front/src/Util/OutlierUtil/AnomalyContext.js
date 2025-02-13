// src/Util/AnomalyContext.js
import React, { createContext, useState } from 'react';

export const AnomalyContext = createContext();

export const AnomalyProvider = ({ children }) => {
  const [newAnomaly, setNewAnomaly] = useState(false);  // newAnomaly 상태 정의

  return (
    <AnomalyContext.Provider value={{ newAnomaly, setNewAnomaly }}>
      {children}
    </AnomalyContext.Provider>
  );
};
