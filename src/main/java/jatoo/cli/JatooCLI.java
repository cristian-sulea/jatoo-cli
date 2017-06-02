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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class JatooCLI extends AbstractCommand {

  public static void main(String[] args) {
    new JatooCLI().execute(args);
  }

  private final Map<String, AbstractCommand> commands = new LinkedHashMap<>();

  public JatooCLI() {
    for (String command : new String[] { "image" }) {
      try {
        commands.put(command, (AbstractCommand) Class.forName("jatoo.cli." + command + ".JatooCLICommand").newInstance());
      } catch (Throwable t) {
        commands.put(command, null);
      }
    }
  }

  @Override
  public void execute(String[] args) {

    //
    // options

    OptionGroup commandsOptionGroup = new OptionGroup();
    commandsOptionGroup.setRequired(true);

    commandsOptionGroup.addOption(Option.builder("help").desc(getText("option.help.desc")).build());

    for (String option : commands.keySet()) {
      AbstractCommand command = commands.get(option);

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

        for (String option : commands.keySet()) {

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
