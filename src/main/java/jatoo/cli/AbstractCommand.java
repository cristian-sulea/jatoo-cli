/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
