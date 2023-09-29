package ch.ictrust.pobya.clam.models

enum class ClamDbType(val value: String) {
    MAIN("main.cvd"),
    DAILY("daily.cvd"),
    BYTECODE("bytecode.cvd")
}