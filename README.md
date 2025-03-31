# 1. Introduction
This work proposes a verifiable timed adapter signature scheme and designs an efficient and provably secure payment channel system that does not rely on scripting languages.

The specific contributions of this work are as follows:
1. Verifiable timed adapter signature with specific instances. Combining adapter signatures, time lock puzzles and non-interactive zero-knowledge proofs, a verifiable timed adapter signature is constructed, and the signature generation time complexity is reduced from O(n) to O(1), and the verifiability of signature recovery after the lock time is achieved.
2. General construction of privacy-preserving scriptless PCN. Based on the verifiable timed signature technology, an efficient and scalable payment system is constructed, which can effectively support large-scale small transaction payment needs, simplify the payment process, and is compatible with various blockchain platforms.
3. Design and implementation of efficient payment channels based on verifiable timed adapter signatures. DApp is constructed based on an efficient and scalable payment system based on verifiable timed adapter signatures. It can achieve privacy protection and efficient off-chain payment between the two parties without relying on any scripting language.

# 2. How to use it
This project is implemented in Java and Maven is responsible for dependency management. You need to first install JDK and Maven on your device, and then perform the following steps:

1. Clone the project to your local device.
```
git clone https://github.com/zllwhu/VTASChannel-server.git
cd VTASChannel-server
```
2. Use Maven to build the project.
```
mvn clean
mvn compile
```
3. Run the project.