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

/**
 * Base class for CLI command implementations.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.2, June 2, 2017
 */
public abstract class AbstractCLICommand {

  private final ResourcesTexts texts = new ResourcesTexts(getClass());

  private final CommandLineParser parser;
  private final HelpFormatter formatter;

  public AbstractCLICommand() {

    parser = new DefaultParser();

    formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    formatter.setWidth(-1);
  }

  public String getDesc() {
    return getText("desc");
  }

  public abstract void execute(final String[] args);

  protected void printHelp(final Options options) {
    printHelp(null, options);
  }

  protected void printHelp(final String command, final Options options) {

    String cmdLineSyntax;

    if (command == null || command.trim().isEmpty()) {
      cmdLineSyntax = "jatoo";
    }

    else {
      cmdLineSyntax = "jatoo " + command.trim();
    }

    formatter.printHelp(cmdLineSyntax, options, true);
  }

  protected void printHelp(final Options options, final Throwable t) {
    printHelp(null, options, t);
  }

  protected void printHelp(final String command, final Options options, final Throwable t) {

    System.out.println(t.getMessage());
    System.out.println();

    printHelp(command, options);
  }

  protected String getText(final String key) {
    return texts.getText(key);
  }

  protected String getText(final String key, final Object... arguments) {
    return texts.getText(key, arguments);
  }

  protected CommandLine parse(final Options options, final String[] arguments) throws ParseException {
    return parser.parse(options, arguments);
  }

  protected CommandLine parse(final Options options, final String[] arguments, final boolean stopAtNonOption) throws ParseException {
    return parser.parse(options, arguments, stopAtNonOption);
  }

  protected void throwUnknownOption() {
    throw new IllegalStateException("unknown option (should never reach this point)");
  }

}
