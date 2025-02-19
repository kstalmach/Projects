#include "utils.h"
#include <iostream>
#include <fstream>
#include <ctime>

void printSeparator() {
    std::cout << "--------------------------------------\n";
}

std::string getCurrentTime() {
    std::time_t now = std::time(nullptr);
    char buf[20];
    std::strftime(buf, sizeof(buf), "%Y-%m-%d %H:%M:%S", std::localtime(&now));
    return std::string(buf);
}

void saveLog(const std::string& message) {
    std::ofstream logFile(LOG_FILE, std::ios::app);
    if (logFile) {
        logFile << "[" << getCurrentTime() << "] " << message << std::endl;
    }
}

void logError(const std::string& errorMessage) {
    std::ofstream logFile(LOG_FILE, std::ios::app);
    if (logFile) {
        logFile << "[" << getCurrentTime() << "] ERROR: " << errorMessage << std::endl;
    }
}
