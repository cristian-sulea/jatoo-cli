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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class JatooCLI extends AbstractCommand {

  private final Map<String, AbstractCommand> commands = new LinkedHashMap<>();

  public JatooCLI() {
    for (String command : new String[] { "image", "image2" }) {
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
