package com.kjipo

import java.io.FileInputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.notExists

class Config(val indexFileDirectory: Path) {

    companion object {

        fun getConfig(config: String = "search_config.properties"): Config {
            val configPath = Paths.get(config)

            if (configPath.notExists()) {
                throw IllegalArgumentException("Config file not found: $config")
            }

            val properties = Properties()
            FileInputStream(configPath.toFile()).use { fileInputStream ->
                properties.load(fileInputStream)
            }


            val indexDirectory = properties.getProperty("indexDirectory")
                ?: throw IllegalArgumentException("Config file is missing property indexDirectory")

            val indexDirectoryPath = Paths.get(indexDirectory)

            if (indexDirectoryPath.notExists()) {
                throw IllegalArgumentException("The path indexDirectory does not exist")
            }

            return Config(indexDirectoryPath)

        }


    }


}