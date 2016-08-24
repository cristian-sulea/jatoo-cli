package jatoo.cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import jatoo.image.ImageFileFilter;
import jatoo.image.ImageUtils;

public class jatoo {

  public static void main(String[] args) {
    new jatoo(args);
  }

  private final CommandLineParser parser;
  private final HelpFormatter formatter;

  private jatoo(final String[] args) {

    parser = new DefaultParser();

    formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    formatter.setWidth(-1);

    //
    // options

    OptionGroup commands = new OptionGroup();
    commands.setRequired(true);
    commands.addOption(Option.builder("help").desc("display this help").build());
    commands.addOption(Option.builder("image").desc("work with images").build());

    Options options = new Options();
    options.addOptionGroup(commands);

    //
    // parse

    try {

      CommandLine line = parser.parse(options, args, true);

      //
      // and work

      if (line.hasOption("help")) {
        printHelp("jatoo", options);
      }

      else if (line.hasOption("image")) {
        image(line.getArgs());
      }

      else {

      }
    }

    catch (Throwable e) {
      printHelp("jatoo", options, e);
      return;
    }
  }

  private void image(final String[] args) {

    //
    // options

    OptionGroup commands = new OptionGroup();
    commands.setRequired(true);
    commands.addOption(Option.builder("resize").desc("resize the image(s)").build());
    commands.addOption(Option.builder("crop").desc("crop a rectangle from the specified image(s)").build());
    commands.addOption(Option.builder("rotate").desc("rotate the image(s)").build());

    Options options = new Options();
    options.addOptionGroup(commands);

    //
    // parse

    try {

      CommandLine line = parser.parse(options, args, true);

      //
      // and work

      if (line.hasOption("resize")) {
        imageResize(line.getArgs());
      }

      else {

      }
    }

    catch (Throwable e) {
      printHelp("jatoo -image", options, e);
    }
  }

  private void imageResize(final String[] args) {

    //
    // options

    OptionGroup commands = new OptionGroup();
    commands.setRequired(true);
    commands.addOption(Option.builder("fit").desc("resize to fit inside a rectangle (keeping the original ratio)").build());
    commands.addOption(Option.builder("fill").desc("resize to fill a rectangle (keeping the original ratio and removing margins from image if needed)").build());

    Options options = new Options();
    options.addOptionGroup(commands);

    //
    // parse

    try {

      CommandLine line = parser.parse(options, args, true);

      //
      // and work

      if (line.hasOption("fit")) {
        imageResizeFit(line.getArgs());
      }

      else {

      }
    }

    catch (Throwable e) {
      printHelp("jatoo -image -resize", options, e);
    }
  }

  private void imageResizeFit(final String[] args) {

    //
    // options

    Options options = new Options();
    options.addOption(Option.builder("width").hasArg().required().desc("maximum width of the resized image (in pixels)").build());
    options.addOption(Option.builder("height").hasArg().required().desc("maximum height of the resized image (in pixels)").build());
    options.addOption(Option.builder("in").hasArg().required().desc("a file (or a folder) with the image(s) to be resized").build());
    options.addOption(Option.builder("out").hasArg().required().desc("a folder where the file(s) with the resized image(s) to be created").build());
    options.addOption(Option.builder("overwrite").desc("overwrite existing file(s)").build());

    //
    // parse

    try {

      CommandLine line = parser.parse(options, args, true);

      //
      // and work

      final int width = Integer.parseInt(line.getOptionValue("width"));
      final int height = Integer.parseInt(line.getOptionValue("height"));
      final File in = new File(line.getOptionValue("in"));
      final File out = new File(line.getOptionValue("out"));
      final boolean overwrite = line.hasOption("overwrite");

      if (!in.exists()) {
        throw new FileNotFoundException("input does not exists: " + in.getAbsolutePath());
      }

      if (!out.exists()) {
        out.mkdirs();
      }
      if (!out.isDirectory()) {
        throw new NotDirectoryException(out.getAbsolutePath());
      }

      if (in.isFile()) {

        File inImageFile = in;
        File outImageFile = new File(out, inImageFile.getName());

        if (!overwrite) {
          if (outImageFile.exists()) {
            throw new FileAlreadyExistsException(outImageFile.getAbsolutePath());
          }
        }

        System.out.println("Resizing " + inImageFile.getName() + " image...");

        String formatName = inImageFile.getName().substring(inImageFile.getName().lastIndexOf('.') + 1);

        BufferedImage inImage = ImageUtils.read(inImageFile);
        BufferedImage outImage = ImageUtils.resizeToFit(inImage, width, height);
        ImageUtils.write(outImage, formatName, outImageFile);

        System.out.println("Done.");
      }

      else if (in.isDirectory()) {

        File[] inImageFiles = in.listFiles(ImageFileFilter.getInstance());

        if (!overwrite) {
          for (File inImageFile : inImageFiles) {
            File outImageFile = new File(out, inImageFile.getName());
            if (outImageFile.exists()) {
              throw new FileAlreadyExistsException(outImageFile.getAbsolutePath());
            }
          }
        }

        System.out.println("Resizing " + inImageFiles.length + " images...");

        for (File inImageFile : inImageFiles) {
          File outImageFile = new File(out, inImageFile.getName());

          String formatName = inImageFile.getName().substring(inImageFile.getName().lastIndexOf('.') + 1);

          BufferedImage inImage = ImageUtils.read(inImageFile);
          BufferedImage outImage = ImageUtils.resizeToFit(inImage, width, height);
          ImageUtils.write(outImage, formatName, outImageFile);

          System.out.println(outImageFile.getName());
        }

        System.out.println("Done.");
      }

      else {
        throw new IllegalArgumentException("illegal input");
      }
    }

    catch (Throwable e) {
      printHelp("jatoo -image -resize -fit", options, e);
    }
  }

  //
  // print help

  private void printHelp(String command, Options options) {
    formatter.printHelp(command, options, true);
  }

  private void printHelp(String command, Options options, Throwable t) {
    System.out.println(t.toString());
    System.out.println();

    printHelp(command, options);

    System.out.println();
    t.printStackTrace(System.out);
  }

}
