# GITHUB ACTIONS SECURITY LANGUAGE SERVER ??
This is a Language server developed to identify security vulnerabilities inside GitHub workflow files. It is used as part of the VSCode extension ??, found [here](https://github.com/hugh-clements/github-actions-vscode-extension).

## Usage
?? follows the LSP spec and uses stdio for communication. It can be used with any client or IDE that supports this, however, it is built to be used through the VSCode extension [??]().

The server <br>
The JAR can be run with `java -jar jarNameAndPath.jar` and requires support for Java 23.

## RULES
The following rules have been implemented as part of the language server. <br>
<br>
To add additional rules, a class implementing the interface `DiagnosticProvider` needs to be created and added to the `DiagnosticService` class constructor. The method `diagnose()` inside the `NewRuleDiagnosticsProvider` performs the diagnostic.

### INCORRECT_LANG
This rule is broken if the file being tracked by the language sever is not in the `YAML` language.<br>
This does not introduce a vulnerability but is a configuration fault.

### INCORRECT_DIRECTORY
This rule is broken if the file being tracked by the language sever is not in the `github/workflows` directory. 
This does not introduce a vulnerability but is a configuration fault.

### NOT_VALID_YAML
This rule is broken if the file being tracked by the language sever is not valid `YAML`.<br>
This does not introduce a vulnerability but is a configuration fault.

### COMMAND_EXECUTION
Vulnerability: A value inserted into a `run:` using `${{}}` is not sanitised and can lead to command execution inside the step, such as `echo Printing ${{user_input}}`.

**Mitigation:**
- Pass as an argument through a `with` clause
- Pass through from an `env` variable

Example vulnerable workflow:
```


```
### CODE_INJECTION
Vulnerability: 

**Mitigation:**

Example vulnerable workflow:
```


```
### REPOJACKABLE
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### PWN_REQUEST
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### RUNNER_HIJACKER
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### PERMISSION_CONTROL
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### UNPINNED_ACTION
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### WORKFLOW_RUN
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### UNSAFE_INPUT_ASSIGNMENT
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```
### OUTDATED_REFERENCE
Vulnerability:

**Mitigation:**

Example vulnerable workflow:
```


```

