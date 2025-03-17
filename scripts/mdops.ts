// After updating run 'scripts/mdops recompile'
import { allInOne } from "https://raw.githubusercontent.com/mdops-org/mdops-cli/main/mod.ts";

if (import.meta.main) {
  allInOne({
    opsFile: "README.md",
    dependenciesSelector: 'heading[value="Dependencies"] > table',
    tasksSelector: 'heading[value="Tasks"] > heading',
  });
}
