import datetime

import sublime, sublime_plugin
from http.server import BaseHTTPRequestHandler, HTTPServer
import json, _thread, threading
import platform, os, sys, time

tests_file_suffix = ''
totalProblems, problems_parsed, successful_problems = 1, 0, 0
settings, user_settings = None, None
contest_name, contest_dir, working_dir, error, parse_in_view_file, view_file_name, sep = None, None, None, False, False, None, False
problems = []




# close create info ?
close_message = 0
debug_mode = 0

create_time = None
create_author = None
create_url = None
globa_test_file = None
globa_targer_file = None



def reset():
    global totalProblems, problems_parsed, successful_problems, contest_name, contest_dir, working_dir, error, parse_in_view_file, view_file_name, sep, problems
    totalProblems, problems_parsed, successful_problems = 1, 0, 0
    contest_name, contest_dir, working_dir, error, parse_in_view_file, view_file_name, sep = None, None, None, False, False, None, False
    problems = []



def plugin_loaded():
    update_settings()
    settings.add_on_change('extensions_path', update_settings)


def show_msg(msg):
    if close_message:
        return
    global sep
    if not sep:
        sep = True
        print(5 * '\n' + '------------------------------START------------------------------')
    sublime.active_window().run_command('show_panel', {"panel": "console"})
    time.sleep(1)
    print(msg)


def close_panel():
    if not error:
        show_msg('closing console...')
        time.sleep(10)
        sublime.active_window().run_command('hide_panel')
        print('closed console')
    print('------------------------------END------------------------------')


def GetSettings(key):
    global settings, user_settings
    try:
        if user_settings.get(key) != None:
            return user_settings.get(key)
        return settings.get(key)
    except:
        return None


def update_settings():
    global settings, user_settings, tests_file_suffix
    settings = sublime.load_settings('CompetitiveProgrammingParser ({os}).sublime-settings'.format(
        os={'windows': 'Windows', 'linux': 'Linux', 'osx': 'OSX'}[sublime.platform().lower()])
    )
    user_settings = sublime.load_settings('CompetitiveProgrammingParser.sublime-settings')
    if GetSettings('tests_file_suffix') != None:
        tests_file_suffix = GetSettings('tests_file_suffix')
    else:
        raise Exception('tests_file_suffix not found in settings file')
    print("CompetitiveProgrammingParser Settings loaded successfully")


# fetch current and working directories
def fetch_directory(oj, action):
    global contest_name, contest_dir, working_dir, error
    if contest_dir == None:  # implies that command wasn't invoked from the sidebar
        key = 'default' if GetSettings('use_default_directory') else oj
        if key not in GetSettings('directory').keys() or GetSettings('directory')[key] == '':
            contest_dir = os.path.dirname(__file__)
        else:
            contest_dir = GetSettings('directory')[key]
    if not os.path.exists(contest_dir):
        os.mkdir(contest_dir)
    working_dir = contest_dir
    if action == 'contest':
        working_dir = os.path.join(working_dir, contest_name)
        try:
            if not os.path.exists(working_dir):
                os.mkdir(working_dir)
        except Exception as e:
            error = True
            raise Exception(str(e) + '\nPlease update your CompetitiveProgrammingParser settings.')

    if GetSettings('open_in_new_window') and action == 'contest':
        os.system('subl -n \"' + working_dir + '\"')
    else:
        os.system('subl -a \"' + working_dir + '\"')


def create_lang_template(filename, full_file_name):
    if not filename or not full_file_name or not GetSettings('lang_extension'):
        return
    try:
        template_content  = None
        template_dir = GetSettings("template_dir") if  GetSettings("template_dir") else full_file_name.replace(full_file_name,filename)
        ext = GetSettings('lang_extension')
        override = GetSettings('override_file')
        ext_name = ext.replace(".", " ").replace(' ', '')
        if not ext_name:
            return
        template_suf = GetSettings("lang_template_suffix") if GetSettings("lang_template_suffix") else '_template.txt'
        template_file = ext_name + template_suf
        if template_file is None or not os.path.isabs(template_file):
            template_file = template_dir + '\\' + template_file

        template_file = template_file.replace(' ', '')
        support_list = GetSettings("support_list") if GetSettings("support_list") else ("cpp","java","c","go","py","js","c#")
        # print('template_file',template_file,'template_content',template_content)
        # show_msg('parse template ... ' + template_file)

        is_replace_file_name = False
        if ext_name == 'java':
            is_replace_file_name = True
        elif ext_name in support_list:
            is_replace_file_name = False
        try:

            #  template template content
            if template_file and os.path.exists(template_file):
                f = open(template_file, 'r')
                template_content = f.read()
                if template_content:
                    if is_replace_file_name and template_content:
                        template_content = template_content.replace('Template', filename.replace(ext, ""))
                    # replace author
                    if GetSettings('create_author'):
                        template_content = template_content.replace('{create_author}', GetSettings('create_author'))

                    # replace create time
                    if GetSettings('create_time') is None:
                        template_content = template_content.replace('{create_time}', datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))

                    # replace create_url
                    if GetSettings('create_url') is None:
                        template_content = template_content.replace('{create_url}', create_url)


                f.close()
        except Exception as e:
            show_msg('❌ parse template error')
            print('error', e)

        if override or (not os.path.exists(full_file_name)):
            file = open(full_file_name, 'w')
            if template_content:
                file.write(str(template_content))
                show_msg('✔️ use ' + template_file + " create success")
            file.close()
    except Exception as e:
        show_msg('❌ use template create error' + e)
        if not os.path.exists(full_file_name):
            file = open(full_file_name, 'w')
            file.close()


# create file and testcases
def parse_testcases(tests, problem, action):
    filename = problem + GetSettings('lang_extension')
    if parse_in_view_file:
        filename = view_file_name
    origin_name = filename
    filename = os.path.join(working_dir, filename)

    # origin method
    # if not os.path.exists(filename):
    #     file = open(filename, 'w')
    #     file.close()


    # my custom template
    try:
        global globa_targer_file,globa_test_file
        globa_targer_file = filename
        create_lang_template(origin_name, filename)

    except:
        pass

    try:
        testcases = []
        tests = tests["tests"]
        for test in tests:
            testcase = {
                "test": test["input"],
                "correct_answers": [test["output"].strip()]
            }
            testcases.append(testcase)
        globa_test_file = filename + tests_file_suffix
        if len(testcase) > 0:
            show_msg('✔️ parse testcase success : ' + json.dumps(testcases))
            
        with open(filename + tests_file_suffix, "w") as f:
            f.write(json.dumps(testcases))
        global successful_problems
        successful_problems += 1

    except Exception as e:
        show_msg('❌ parse testcase error')


def check_page_correctness(action):
    global error
    if totalProblems > 1 and action != 'contest':
        error = True
        raise Exception('It seems that you are trying to parse a contest page. Please open a problem page!')


def get_problem_name(tests, oj):
    global contest_name
    if debug_mode:
        show_msg('get_problem_name ...')
    problem = tests["name"]
    if contest_name == None:
        contest_name = tests["group"].split('-', 1)[-1].strip()
        show_msg('Contest: ' + contest_name)
    if oj == "CodeChef":
        problem = tests["url"].split('/')[-1]
    elif oj == "Yandex":
        problem = tests["name"].split('.')[0]
    elif oj == 'Codeforces':
        if 'problem/' in tests['url']:
            problem = tests["url"].split('problem/')[1]
            problem = problem.replace('problem','').replace('contest','').replace('//','/').replace('/','_')
            problem = str(problem).upper()
        else:
            problem = tests["name"].split('.')[0]
    # elif oj == "Codeforces" or oj == "Yandex":
    #     problem = tests["name"].split('.')[0]
    elif oj == "AtCoder":
        problem = tests["name"].split(' ')[0]
    else:
        problem = tests["url"].split('/')[-1]
    if problem[0].isdigit():
            problem = '_' + problem
    problem = problem.replace(" ", "_")
    global create_url
    create_url = tests["url"]
    return problem


def handle(tests, action):
    global totalProblems, problems_parsed, error
    problems_parsed += 1
    totalProblems = tests["batch"]['size']
    show_msg('🥰 parse total Info ...  ')
    if GetSettings("print_total_info"):
        show_msg(json.dumps(tests))

    try:
        check_page_correctness(action)
    except Exception as e:
        raise Exception(e)
    oj = tests["group"].split('-')[0].strip()
    problem = get_problem_name(tests, oj)
    cnt = problems.count(problem)
    if cnt == 5:
        error = True
        show_msg('❌ Aborting the process. Please check your internet connection and try again')
        return
    elif cnt > 0:
        problems.append(problem)
        show_msg('❌ Could not parse the next problem.(possibly due to slow internet connection).\nTrying again(' + str(
            cnt) + ')...')
        problems_parsed -= 1
        return
    else:
        problems.append(problem)

    if problems_parsed == 1 and action != 'testcase':
        try:
            fetch_directory(oj, action)
        except Exception as e:
            raise Exception(e)
        show_msg('parsing ' + action + "...")

    try:
        parse_testcases(tests, problem, action)
    except Exception as e:
        show_msg("❌ Problem " + problem + " (" + str(problems_parsed) + "/" + str(totalProblems) + ")" + " fail")


def MakeHandlerClass(action):
    class HandleRequests(BaseHTTPRequestHandler):
        def do_POST(self):
            try:
                handle(json.loads(self.rfile.read(int(self.headers['Content-Length'])).decode('utf8')), action)
                if globa_test_file:
                    show_msg('✔️ tescase save location:' + globa_test_file)
                if globa_targer_file:
                    show_msg('✔️ file   save  location:' + globa_targer_file)
            except Exception as e:
                show_msg("❌ error: " + str(e))
            threading.Thread(target=self.server.shutdown, daemon=True).start()

    return HandleRequests


class CompetitiveCompanionServer:
    def startServer(action):
        try:
            httpd = HTTPServer(('localhost', 12345), MakeHandlerClass(action))
            while problems_parsed < totalProblems and not error:
                httpd.serve_forever()
            if action == 'contest' and successful_problems > 0:
                x = "All" if successful_problems == totalProblems else "Only"
                show_msg(x + " (" + str(successful_problems) + "/" + str(totalProblems) + ") Problems of \'" + str(
                    contest_name) + "\'" + ' were parsed successfully')
            close_panel()
        except Exception as e:
            pass


class CompetitiveProgrammingParserFileCommand(sublime_plugin.TextCommand):
    def run(self, edit, action):
        global error
        reset()
        try:
            if action == 'testcase' and self.view.file_name() == None:
                error = True
                raise Exception("Can't parse testcases for an untitled file.")

            if action == 'testcase':
                global parse_in_view_file, view_file_name
                parse_in_view_file = True
                view_file_name = self.view.file_name()

            if GetSettings('lang_extension') == None:
                error = True
                raise Exception('Language not set. Update your CompetitiveProgrammingParser settings.')

            _thread.start_new_thread(CompetitiveCompanionServer.startServer, (action,))

        except Exception as e:
            show_msg("❌ error: " + str(e))
            close_panel()


class CompetitiveProgrammingParserSidebarCommand(sublime_plugin.WindowCommand):
    def run(self, dirs, action, **kwargs):
        reset()
        global contest_dir, error
        contest_dir = dirs[0]
        try:
            if GetSettings('lang_extension') == None:
                error = True
                raise Exception('language extension not set. Update your CompetitiveProgrammingParser settings.')
            _thread.start_new_thread(CompetitiveCompanionServer.startServer, (action,))
        except Exception as e:
            show_msg("❌ error: " + str(e))
            close_panel()

    def is_enabled(self, dirs, action):
        return len(dirs) == 1

    def is_visible(self, dirs, action):
        return len(dirs) == 1
