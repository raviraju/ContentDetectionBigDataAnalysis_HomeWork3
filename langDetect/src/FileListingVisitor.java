import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/** Recursive listing with SimpleFileVisitor in JDK 7. */
public final class FileListingVisitor {
  
  public static void main(String args[]) throws IOException{
    String ROOT = args[0];
    FileVisitor<Path> fileProcessor = new ProcessFile();
    Files.walkFileTree(Paths.get(ROOT), fileProcessor);
  }

  private static final class ProcessFile extends SimpleFileVisitor<Path> {
    @Override public FileVisitResult visitFile(
      Path aFile, BasicFileAttributes aAttrs
    ) throws IOException {
      System.out.println("Processing file:" + aFile);
      return FileVisitResult.CONTINUE;
    }
    
    @Override  public FileVisitResult preVisitDirectory(
      Path aDir, BasicFileAttributes aAttrs
    ) throws IOException {
      System.out.println("Processing directory:" + aDir);
      return FileVisitResult.CONTINUE;
    }
  }
} 
