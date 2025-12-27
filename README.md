# Kickstart Offense with Minion's Best in an Amicable Territory (KOMBAT)
### [First version repository](https://github.com/PpGamerer/KOMBAT)

## Original Project Specification
This project was originally developed based on a formal course specification.

[Full Project Specification (PDF)](project_specifications.pdf)

[Game Overview (PDF)](frontend/public/overview.pdf)

# Project Background
In the first version, I developed the complete backend system for the game, including:
- Core turn-based game logic

- Custom minion strategy scripting and parsing

- Rule evaluation and game state management

_This version was fully playable in a terminal environment._

The frontend created during the course was a UI mock / prototype, supporting only basic interactions such as:

- Character selection
- Hex purchasing
- It did not support full gameplay or rule execution.

__After the course, I extended the project independently by building Spring Boot REST APIs to connect the existing backend logic with the frontend, enabling actual gameplay through a web interface (local single-browser mode).__

Tech Stack: Java (OOP), Spring Boot, REST APIs

Note: Multiplayer across multiple browsers or machines was part of the original course scope but is not implemented in this version.