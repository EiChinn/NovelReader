package com.example.newbiechen.ireader.utils

import android.os.Environment
import com.example.newbiechen.ireader.App
import java.io.*
import java.text.DecimalFormat

object FileUtils {
    //采用自己的格式去设置文件，防止文件被系统文件查询到
    const val SUFFIX_NB = ".nb"
    const val SUFFIX_TXT = ".txt"


    @JvmStatic
    fun getFolder(filePath: String): File {
        val file = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    //获取文件
    @JvmStatic
    @Synchronized
    fun getFile(filePath: String): File {
        val file = File(filePath)
        try {
            if (!file.exists()) {
                //创建父类文件夹
                getFolder(file.parent)
                //创建文件
                file.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    //获取Cache文件夹
    @JvmStatic
    fun getCachePath(): String {
        return if (isSdCardExist()) {
            App.getInstance()
                    .externalCacheDir!!
                    .absolutePath
        } else {
            App.getInstance()
                    .cacheDir
                    .absolutePath
        }
    }

    //判断是否挂载了SD卡
    private fun isSdCardExist(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    @JvmStatic
    fun getDirSize(file: File): Long {
        //判断文件是否存在
        return if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory) {
                val children = file.listFiles()
                var size: Long = 0
                for (f in children)
                    size += getDirSize(f)
                size
            } else {
                file.length()
            }
        } else {
            0
        }
    }


    @JvmStatic
    fun getFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("b", "kb", "M", "G", "T")
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        val digitGroups = Math.log10(size.toDouble()) / Math.log10(1024.0)
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return DecimalFormat("#,##0.##").format(size / Math.pow(1024.0, digitGroups)) + " " + units[digitGroups.toInt()]
    }

    @JvmStatic
    @Synchronized
    fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            return
        }
        file.deleteRecursively()
    }

    @JvmStatic
    fun getCharset(fileName: String): Charset {
        val first3Bytes = ByteArray(3)
        val bis = BufferedInputStream(FileInputStream(fileName))
        try {
            bis.use {
                bis.mark(0)
                var read = bis.read(first3Bytes, 0, 3)
                if (read == -1) {
                    return Charset.GBK
                }
                if (first3Bytes[0] == 0xEF.toByte()
                        && first3Bytes[1] == 0xBB.toByte()
                        && first3Bytes[2] == 0xBF.toByte()) {
                    return Charset.UTF8
                } else if (first3Bytes[0] == 0xFF.toByte() && first3Bytes[1] == 0xFE.toByte()) {
                    return Charset.UTF16LE
                } else if (first3Bytes[0] == 0xFE.toByte() && first3Bytes[1] == 0xFF.toByte()) {
                    return Charset.UTF16BE
                }

                bis.reset()
                read = bis.read()
                while (read != -1) {
                    if (read >= 0xF0)
                        break
                    if (read in 0x80..0xBF)
                    // 单独出现BF以下的，也算是GBK
                        break
                    if (read in 0xC0..0xDF) {
                        read = bis.read()
                        if (read in 0x80..0xBF) {
                            // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            read = bis.read()
                            continue
                        } else
                            break
                    } else if (read in 0xE0..0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read()
                        if (read in 0x80..0xBF) {
                            read = bis.read()
                            return if (read in 0x80..0xBF) {
                                Charset.UTF8
                            } else
                                break
                        } else
                            break
                    }
                }


            }
        } catch (e: Exception) {

        }
        return Charset.GBK


    }

    /**
     * 本来是获取File的内容的。但是为了解决文本缩进、换行的问题
     * 这个方法就是专门用来获取书籍的...
     *
     * 应该放在BookRepository中。。。
     * @param file
     * @return
     */
    @JvmStatic
    fun getFileContent(file: File): String {
        var str: String?
        val sb = StringBuilder()
        try {
            val reader = FileReader(file)
            reader.use {
                val br = BufferedReader(reader)
                br.use {
                    str = br.readLine()
                    while (str != null) {
                        //过滤空语句
                        if (str != "") {
                            //由于sb会自动过滤\n,所以需要加上去
                            sb.append("    $str\n")
                        }
                        str = br.readLine()
                    }
                }

            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

}
