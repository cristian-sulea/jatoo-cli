package jatoo.cli;

public class ImageTests {

  public static void main(String[] args) {
    
//    jatoo.main(
//        new String[] { "-image", "-resize", "-fit" });
    
    jatoo.main(
        new String[] {
            "-image", "-resize", "-fit",
            "-width", "800",
            "-height", "600",
            "-in", "src/test/resources/jatoo/cli/image/20141109144518.jpg",
            "-out", "target/tests",
            "-overwrite" });
  }

}
