import sys
import re
import os
import shutil
from manifest_parser import *


def get_old_pkg_name(container_app_folder):
	app_Manifest = container_app_folder + "/app/src/main/AndroidManifest.xml"
	return get_pkg_name(app_Manifest)

def change_package_name(container_app_folder, new_pkg_name):
	
	#First, handle the MainActivity.java
	old_pkg_name = get_old_pkg_name(container_app_folder)
	old_pkg_path = old_pkg_name.replace(".", "/")
	old_app_src_module_path = container_app_folder + "/app/src/main/java/" + old_pkg_path + "/"

	new_pkg_path = new_pkg_name.replace(".", "/")
	new_app_src_module_path = container_app_folder + "/app/src/main/java/" + new_pkg_path + "/"

	new_folder = os.path.dirname(new_app_src_module_path)
	if not os.path.exists(new_folder):
		os.makedirs(new_folder)

	find_and_replace_2(old_app_src_module_path + "MainActivity.java", new_app_src_module_path + "MainActivity.java", old_pkg_name, new_pkg_name)	
	find_and_replace_1(container_app_folder + "/app/src/main/AndroidManifest.xml", old_pkg_name, new_pkg_name)
	find_and_replace_1(container_app_folder + "/DroidPlugin/build.gradle", old_pkg_name, new_pkg_name)
	find_and_replace_1(container_app_folder + "/app/build.gradle", old_pkg_name, new_pkg_name)


def find_and_replace_1(file_1, word_to_find, word_to_replace):
	f1 = open(file_1, 'r')
	f2 = open('tmp.xml', 'w')
	for line in f1:
		f2.write(line.replace(word_to_find, word_to_replace))
	f1.close()
	f2.close()

	shutil.copyfile('tmp.xml', file_1)
	os.remove('tmp.xml')


def find_and_replace_2(file_1, file_2, word_to_find, word_to_replace):
	f1 = open(file_1, 'r')
	f2 = open(file_2, 'w')
	for line in f1:
		f2.write(line.replace(word_to_find, word_to_replace))
	f1.close()
	f2.close()

def change_app_name(container_app_folder, new_pkg_name):
	delims = "[.]+"
	splits = filter(None, re.split(delims, new_pkg_name))
	app_name = splits[-1]
	find_and_replace_1(container_app_folder + "/app/src/main/res/values/strings.xml", "HMA-App1", "HMA - " + app_name)		
	
if __name__ == "__main__":
    container_app_folder = sys.argv[1]
    new_pkg_name = sys.argv[2]
    change_package_name(container_app_folder, new_pkg_name)
    change_app_name(container_app_folder, new_pkg_name)
