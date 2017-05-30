/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import jatoo.resources.ResourcesTexts;

public class jatoo {

  public static void main(String[] args) {
    new jatoo(args);
  }

  private final ResourcesTexts texts = new ResourcesTexts(getClass());

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
    commands.addOption(Option.builder("help").desc(texts.getText("option.help.desc")).build());
    commands.addOption(Option.builder("image").desc(texts.getText("option.image.desc")).build());

    Options options = new Options();
    options.addOptionGroup(commands);

    //
    // parse

    try {

      CommandLine line = parser.parse(options, args, true);

      //
      // and work

      if (line.hasOption("help")) {
        printHelp(texts.getText("command"), options);
      }

      else if (line.hasOption("image")) {
        image(line.getArgs());
      }

      else {

      }
    }

    catch (Throwable e) {
      printHelp(texts.getText("command"), options, e);
      return;
    }
  }

  private void image(final String[] args) {

    //
    // options

    OptionGroup commands = new OptionGroup();
    commands.setRequired(true);
    commands.addOption(Option.builder("resize").desc(texts.getText("option.image.resize.desc")).build());
    commands.addOption(Option.builder("crop").desc(texts.getText("option.image.crop.desc")).build());
    commands.addOption(Option.builder("rotate").desc(texts.getText("option.image.rotate.desc")).build());

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
      printHelp(texts.getText("command") + " -image", options, e);
    }
  }

  private void imageResize(final String[] args) {

    //
    // options

    OptionGroup commands = new OptionGroup();
    commands.setRequired(true);
    commands.addOption(Option.builder("fit").desc(texts.getText("option.image.resize.fit.desc")).build());
    commands.addOption(Option.builder("fill").desc(texts.getText("option.image.resize.fill.desc")).build());

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
      printHelp(texts.getText("command") + " -image -resize", options, e);
    }
  }

  private void imageResizeFit(final String[] args) {

    //
    // options

    Options options = new Options();
    options.addOption(Option.builder("width").hasArg().required().desc(texts.getText("option.image.resize.fit.width.desc")).build());
    options.addOption(Option.builder("height").hasArg().required().desc(texts.getText("option.image.resize.fit.height.desc")).build());
    options.addOption(Option.builder("in").hasArg().required().desc(texts.getText("option.image.resize.fit.in.desc")).build());
    options.addOption(Option.builder("out").hasArg().required().desc(texts.getText("option.image.resize.fit.out.desc")).build());
    options.addOption(Option.builder("overwrite").desc(texts.getText("option.image.resize.fit.overwrite.desc")).build());

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

        System.out.println(texts.getText("text.resizing.1.image", inImageFile.getName()));

        String formatName = inImageFile.getName().substring(inImageFile.getName().lastIndexOf('.') + 1);

        BufferedImage inImage = ImageUtils.read(inImageFile);
        BufferedImage outImage = ImageUtils.resizeToFit(inImage, width, height);
        ImageUtils.write(outImage, formatName, outImageFile);

//        SIETImageMetadata.copyMetadataTest(inImageFile, outImageFile);

        System.out.println(texts.getText("text.done"));
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

        System.out.println(texts.getText("text.resizing.n.images", inImageFiles.length));

        for (File inImageFile : inImageFiles) {
          File outImageFile = new File(out, inImageFile.getName());

          String formatName = inImageFile.getName().substring(inImageFile.getName().lastIndexOf('.') + 1);

          BufferedImage inImage = ImageUtils.read(inImageFile);
          BufferedImage outImage = ImageUtils.resizeToFit(inImage, width, height);
          ImageUtils.write(outImage, formatName, outImageFile);

          System.out.println(outImageFile.getName());
        }

        System.out.println(texts.getText("text.done"));
      }

      else {
        throw new IllegalArgumentException("illegal input");
      }
    }

    catch (Throwable e) {
      printHelp(texts.getText("command") + " -image -resize -fit", options, e);
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
