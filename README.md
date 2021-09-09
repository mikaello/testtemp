# testtemp

Repo with some test code to test Azure functions code.

* [CommandUtils.java](./app/src/main/java/testtemp/CommandUtils.java) is copied from [github.com/microsoft/azure-maven-plugins/azure-toolkit-libs/azure-toolkit-common-lib/src/main/java/com/microsoft/azure/toolkit/lib/common/utils/CommandUtils.java#L27](https://github.com/microsoft/azure-maven-plugins/blob/47625cbd26f601ded1a82b9ec95b5b073b47ac61/azure-toolkit-libs/azure-toolkit-common-lib/src/main/java/com/microsoft/azure/toolkit/lib/common/utils/CommandUtils.java#L27)
* [FunctionCliResolver](./app/src/main/java/testtemp/FunctionCliResolver.java) is copied from [github.com/microsoft/azure-maven-plugins/azure-toolkit-libs/azure-toolkit-appservice-lib/src/main/java/com/microsoft/azure/toolkit/lib/appservice/utils/FunctionCliResolver.java](https://github.com/microsoft/azure-maven-plugins/blob/47625cbd26f601ded1a82b9ec95b5b073b47ac61/azure-toolkit-libs/azure-toolkit-appservice-lib/src/main/java/com/microsoft/azure/toolkit/lib/appservice/utils/FunctionCliResolver.java)

See issue https://github.com/microsoft/azure-gradle-plugins/issues/111 for reference to why this repository.

Output when running:

```shell
$ gradle run

> Task :app:run
null
[]

BUILD SUCCESSFUL in 703ms
2 actionable tasks: 1 executed, 1 up-to-date

```
