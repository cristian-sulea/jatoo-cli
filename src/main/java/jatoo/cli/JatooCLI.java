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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * The "jatoo" command for the JaToo CLI project.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.2, June 2, 2017
 */
public class JatooCLI extends AbstractCLICommand {

  public static void main(final String[] args) {
    new JatooCLI().execute(args);
  }

  private final Map<String, AbstractCLICommand> commands = new LinkedHashMap<>();

  public JatooCLI() {
    for (String command : Arrays.asList("image", "")) {
      try {
        commands.put(command, (AbstractCLICommand) Class.forName("jatoo.cli." + command + ".JatooCLICommand").newInstance());
      } catch (Throwable t) {
        commands.put(command, null);
      }
    }
  }

  @Override
  public void execute(final String[] args) {

    //
    // options

    OptionGroup commandsOptionGroup = new OptionGroup();
    commandsOptionGroup.setRequired(true);

    commandsOptionGroup.addOption(Option.builder("help").desc(getText("option.help.desc")).build());

    for (Map.Entry<String, AbstractCLICommand> entry : commands.entrySet()) {
      String option = entry.getKey();
      AbstractCLICommand command = entry.getValue();

      if (command == null) {
        continue;
      }

      commandsOptionGroup.addOption(Option.builder(option).desc(command.getDesc()).build());
    }

    Options options = new Options();
    options.addOptionGroup(commandsOptionGroup);

    //
    // parse

    try {

      CommandLine line = parse(options, args, true);

      //
      // and work

      if (line.hasOption("help")) {
        printHelp(options);
      }

      else {

        for (Map.Entry<String, AbstractCLICommand> entry : commands.entrySet()) {
          String option = entry.getKey();

          if (line.hasOption(option)) {
            commands.get(option).execute(line.getArgs());
          }
        }
      }
    }

    catch (Throwable e) {
      printHelp(options, e);
    }
  }

}
