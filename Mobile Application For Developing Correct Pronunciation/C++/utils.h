#ifndef UTILS_H
#define UTILS_H

#include <string>

constexpr char LOG_FILE[] = "log.txt"; 

void printSeparator();
void saveLog(const std::string& message);
void logError(const std::string& errorMessage);
std::string getCurrentTime(); 

#endif // UTILS_H
