// defaultStrategies.ts
export const DEFAULT_STRATEGIES: Record<string, string> = {
    "Charlotte": `energy = budget - 70
loop = 0
while (2 - loop) {
  if (energy - 100) then {} else done
  enemy = opponent
  if (enemy / 10 - 1) then {
    if (enemy % 10 - 5) then move upleft
    else if (enemy % 10 - 4) then move downleft
    else if (enemy % 10 - 3) then move down
    else if (enemy % 10 - 2) then move downright
    else if (enemy % 10 - 1) then move upright
    else move up
  } else if (enemy) then {
    power = 6 ^ (nearby up % 35 + 1)
    if (power - 65) then power = 65 else power = power
    if (budget - power) then shoot up power else done
  } else {
    randomDir = random % 3
    if (randomDir - 2) then move down
    else if (randomDir - 1) then move upleft
    else move upright
  }
  loop = loop + 1
}`,

    "Cody": `energy = budget - 40
step = 0
while (2 - step) {
  if (energy - 80) then {} else done
  enemy = opponent
  if (enemy / 10 - 1) then {
    if (enemy % 10 - 5) then move upleft
    else if (enemy % 10 - 4) then move downleft
    else if (enemy % 10 - 3) then move down
    else if (enemy % 10 - 2) then move downright
    else if (enemy % 10 - 1) then move upright
    else move up
  } else if (enemy) then {
    power = 6 ^ (nearby down % 45 + 2)
    if (power - 65) then power = 65 else power = power
    if (budget - power) then shoot down power else done
  } else {
    randMove = random % 2
    if (randMove) then move downright else move upleft
  }
  step = step + 1
}`,

    "Connie": `energy = budget - 50
step = 0
while (2 - step) {
  if (energy - 80) then {} else done
  enemy = opponent
  if (enemy / 10 - 1) then {
    if (enemy % 10 - 5) then move upleft
    else if (enemy % 10 - 4) then move downleft
    else if (enemy % 10 - 3) then move down
    else if (enemy % 10 - 2) then move downright
    else if (enemy % 10 - 1) then move upright
    else move up
  } else if (enemy) then {
    power = 4 ^ (nearby down % 50 + 1)
    if (power - 60) then power = 60 else power = power
    if (budget - power) then shoot down power else done
  } else {
    turn = random % 2
    if (turn) then move upright else move downleft
  }
  step = step + 1
}`,

    "Cora": `t = t + 1
m = 0
while (3 - m) {
  if (budget - 100) then {} else done
  opponentLoc = opponent
  if (opponentLoc / 10 - 1) then {
    if (opponentLoc % 10 - 5) then move upleft
    else if (opponentLoc % 10 - 4) then move downleft
    else if (opponentLoc % 10 - 3) then move down
    else if (opponentLoc % 10 - 2) then move downright
    else if (opponentLoc % 10 - 1) then move upright
    else move up
  } else if (opponentLoc) then {
    if (opponentLoc % 10 - 5) then {
      cost = 9 ^ (nearby upleft % 80 + 1)
      if (cost - 110) then cost = 110 else cost = cost
      if (budget - cost) then shoot upleft cost else done
    }
    else if (opponentLoc % 10 - 4) then {
      cost = 9 ^ (nearby downleft % 80 + 1)
      if (cost - 110) then cost = 110 else cost = cost
      if (budget - cost) then shoot downleft cost else {}
    }
    else if (opponentLoc % 10 - 3) then {
      cost = 9 ^ (nearby down % 80 + 1)
      if (cost - 110) then cost = 110 else cost = cost
      if (budget - cost) then shoot down cost else {}
    }
    else if (opponentLoc % 10 - 2) then {
      cost = 9 ^ (nearby downright % 80 + 1)
      if (cost - 110) then cost = 110 else cost = cost
      if (budget - cost) then shoot downright cost else {}
    }
    else if (opponentLoc % 10 - 1) then {
      cost = 9 ^ (nearby upright % 80 + 1)
      if (cost - 110) then cost = 110 else cost = cost
      if (budget - cost) then shoot upright cost else {}
    }
    else {
      cost = 9 ^ (nearby up % 80 + 1)
      if (cost - 110) then cost = 110 else cost = cost
      if (budget - cost) then shoot up cost else {}
    }
  } else {
    try = 0
    while (3 - try) {
      success = 1
      dir = random % 6
      if ((dir - 4) * (nearby upleft % 10 + 1) ^ 2) then move upleft
      else if ((dir - 3) * (nearby downleft % 10 + 1) ^ 2) then move downleft
      else if ((dir - 2) * (nearby down % 10 + 1) ^ 2) then move down
      else if ((dir - 1) * (nearby downright % 10 + 1) ^ 2) then move downright
      else if (dir * (nearby upright % 10 + 1) ^ 2) then move upright
      else if ((nearby up % 10 + 1) ^ 2) then move up
      else success = 0
      if (success) then try = 3 else try = try + 1
    }
  }
  m = m + 1
}`,

    "Crystal": `t = t + 1
m = 0
while (3 - m) {
  if (budget - 100) then {} else done
  opponentLoc = opponent
  if (opponentLoc / 10 - 1)
  then
    if (opponentLoc % 10 - 5) then move upleft
    else if (opponentLoc % 10 - 4) then move downleft
    else if (opponentLoc % 10 - 3) then move down
    else if (opponentLoc % 10 - 2) then move downright
    else if (opponentLoc % 10 - 1) then move upright
    else move up
  else if (opponentLoc)
  then
    if (opponentLoc % 10 - 5) then {
      cost = 10 ^ (nearby upleft % 100 + 1)
      if (cost - 100) then cost = 100 else cost = cost
      if (budget - cost) then shoot upleft cost else done
    }
    else if (opponentLoc % 10 - 4) then {
      cost = 10 ^ (nearby downleft % 100 + 1)
      if (cost - 100) then cost = 100 else cost = cost
      if (budget - cost) then shoot downleft cost else {}
    }
    else if (opponentLoc % 10 - 3) then {
      cost = 10 ^ (nearby down % 100 + 1)
      if (cost - 100) then cost = 100 else cost = cost
      if (budget - cost) then shoot down cost else {}
    }
    else if (opponentLoc % 10 - 2) then {
      cost = 10 ^ (nearby downright % 100 + 1)
      if (cost - 100) then cost = 100 else cost = cost
      if (budget - cost) then shoot downright cost else {}
    }
    else if (opponentLoc % 10 - 1) then {
      cost = 10 ^ (nearby upright % 100 + 1)
      if (cost - 100) then cost = 100 else cost = cost
      if (budget - cost) then shoot upright cost else {}
    }
    else {
      cost = 10 ^ (nearby up % 100 + 1)
      if (cost - 100) then cost = 100 else cost = cost
      if (budget - cost) then shoot up cost else {}
    }
  else {
    try = 0
    while (3 - try) {
      success = 1
      dir = random % 6
      if ((dir - 4) * (nearby upleft % 10 + 1) ^ 2) then move upleft
      else if ((dir - 3) * (nearby downleft % 10 + 1) ^ 2) then move downleft
      else if ((dir - 2) * (nearby down % 10 + 1) ^ 2) then move down
      else if ((dir - 1) * (nearby downright % 10 + 1) ^ 2) then move downright
      else if (dir * (nearby upright % 10 + 1) ^ 2) then move upright
      else if ((nearby up % 10 + 1) ^ 2) then move up
      else success = 0
      if (success) then try = 3 else try = try + 1
    }
  }
  m = m + 1
}`
};

// Function to get default strategy by minion name
export const getDefaultStrategy = (minionName: string): string => {
    return DEFAULT_STRATEGIES[minionName] || "";
};