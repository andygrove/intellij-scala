package org.jetbrains.plugins.scala.project

import java.io.File

import com.intellij.openapi.vfs.{VirtualFile, VirtualFileListener, VirtualFileSystem}

/**
 * @author Pavel Fatin
 */
class AbsentLocalFile(url: String, path: String) extends VirtualFile {
  override def getName = throw new UnsupportedOperationException()

  override def getLength = throw new UnsupportedOperationException()

  override def getFileSystem = AbsentLocalFileSystem

  override def contentsToByteArray() = throw new UnsupportedOperationException()

  override def getParent = throw new UnsupportedOperationException()

  override def refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable) =
    throw new UnsupportedOperationException()

  override def getTimeStamp = throw new UnsupportedOperationException()

  override def getOutputStream(requestor: AnyRef, newModificationStamp: Long, newTimeStamp: Long) =
    throw new UnsupportedOperationException()

  override def isDirectory = throw new UnsupportedOperationException()

  override def getPath: String = path

  override def isWritable = throw new UnsupportedOperationException()

  override def isValid = false

  override def getChildren = throw new UnsupportedOperationException()

  override def getInputStream = throw new UnsupportedOperationException()

  override def getUrl: String = url
}

object AbsentLocalFileSystem extends VirtualFileSystem {
  override def getProtocol = throw new UnsupportedOperationException()

  override def renameFile(requestor: AnyRef, vFile: VirtualFile, newName: String) =
    throw new UnsupportedOperationException()

  override def createChildFile(requestor: AnyRef, vDir: VirtualFile, fileName: String) =
    throw new UnsupportedOperationException()

  override def addVirtualFileListener(virtualFileListener: VirtualFileListener) =
    throw new UnsupportedOperationException()

  override def refreshAndFindFileByPath(s: String) = throw new UnsupportedOperationException()

  override def copyFile(requestor: AnyRef, virtualFile: VirtualFile, newParent: VirtualFile, copyName: String) =
    throw new UnsupportedOperationException()

  override def refresh(asynchronous: Boolean) = throw new UnsupportedOperationException()

  override def isReadOnly = throw new UnsupportedOperationException()

  override def createChildDirectory(requestor: AnyRef, vDir: VirtualFile, dirName: String) =
    throw new UnsupportedOperationException()

  override def removeVirtualFileListener(virtualFileListener: VirtualFileListener) =
    throw new UnsupportedOperationException()

  override def moveFile(requestor: AnyRef, vFile: VirtualFile, newParent: VirtualFile) =
    throw new UnsupportedOperationException()

  override def findFileByPath(path: String) = throw new UnsupportedOperationException()

  override def deleteFile(requestor: AnyRef, vFile: VirtualFile) = throw new UnsupportedOperationException()

  override def extractPresentableUrl(path: String): String = path.replace('/', File.separatorChar)
}
