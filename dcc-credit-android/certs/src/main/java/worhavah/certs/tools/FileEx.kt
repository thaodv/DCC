package worhavah.certs.tools

import java.io.File

fun File.ensureNewFile() {
    if (this.exists()) {
        delete()
    } else {
        this.parentFile.mkdirs()
        this.createNewFile()
    }
}