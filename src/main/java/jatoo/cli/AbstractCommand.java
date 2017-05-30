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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jatoo.resources.ResourcesTexts;

public abstract class AbstractCommand {

  private final ResourcesTexts texts = new ResourcesTexts(getClass());

  private final CommandLineParser parser;
  private final HelpFormatter formatter;

  public AbstractCommand() {

    parser = new DefaultParser();

    formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    formatter.setWidth(-1);
  }

  public String getDesc() {
    return getText("desc");
  }

  public abstract void execute(final String[] args);

  protected void printHelp(Options options) {
    printHelp(null, options);
  }

  protected void printHelp(String command, Options options) {

    if (command == null || command.trim().isEmpty()) {
      command = "jatoo";
    }

    else {
      command = "jatoo " + command.trim();
    }

    formatter.printHelp(command, options, true);
  }

  protected void printHelp(Options options, Throwable t) {
    printHelp(null, options, t);
  }

  protected void printHelp(String command, Options options, Throwable t) {

    System.out.println(t.getMessage());
    System.out.println();

    printHelp(command, options);

    System.out.println();
    t.printStackTrace(System.out);
  }

  protected String getText(String key) {
    return texts.getText(key);
  }

  protected String getText(String key, Object... arguments) {
    return texts.getText(key, arguments);
  }

  protected CommandLine parse(Options options, String[] arguments) throws ParseException {
    return parser.parse(options, arguments);
  }

  protected CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
    return parser.parse(options, arguments, stopAtNonOption);
  }

  protected void throwUnknownOption() {
    throw new IllegalStateException("unknown option (should never reach this point)");
  }

}
