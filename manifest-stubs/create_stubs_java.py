#Generate stub activities in StubActivity.java based on what is declared in the updated Manifest file of DroidPlugin
#Parse the manifest file of the container, generate and update the stub activities
import sys
import random
import string
from manifest_parser import *
import re

def gen_stubs(container_file_name):
	ns = get_namespaces(container_file_name)
	for prefix, uri in ns.iteritems():
		ET.register_namespace(prefix, uri)
	
	tree = ET.parse(container_file_name)
	root = tree.getroot()

	activities = get_activities(container_file_name)
	
	ActivityStub_file = container_file_name [:-19] + 'src/com/morgoo/droidplugin/stub/ActivityStub.java'

	new_activity_stubs_str = ""

	new_activity_stubs_str += "package com.morgoo.droidplugin.stub; \n"
	new_activity_stubs_str += "import android.app.Activity;\n"
	new_activity_stubs_str += "public abstract class ActivityStub extends Activity { \n"
	new_activity_stubs_str += "\tprivate static class SingleInstanceStub extends ActivityStub {}\n"
	new_activity_stubs_str += "\tprivate static class SingleTaskStub extends ActivityStub {} \n"
	new_activity_stubs_str += "\tprivate static class SingleTopStub extends ActivityStub {} \n"
	new_activity_stubs_str += "\tprivate static class StandardStub extends ActivityStub {}\n"
	new_activity_stubs_str += "\tpublic static class P08{ \n"

	activity_stubs = gen_activity_stubs(activities)
	new_activity_stubs_str += ''.join(activity_stubs)

	new_activity_stubs_str += "\t}\n"

	new_activity_stubs_str += "\tpublic static class Dialog {\n"
	new_activity_stubs_str += "\t\tpublic static class P08 {\n"
	activity_stubs = gen_activity_stubs(activities)
	new_activity_stubs_str += ''.join(activity_stubs)	
	new_activity_stubs_str += "\t\t}\n"
	new_activity_stubs_str += "\t}\n"
	new_activity_stubs_str += "}\n"

	#Write this new_activity_stubs to the old ActivityStub.java file
	f = open(ActivityStub_file, 'w')
	f.write(new_activity_stubs_str)

def form_class_name(declared_activity):
	r = re.compile("([a-zA-Z]+)([0-9]+)")
	return r.match(declared_activity).groups()[0] + "Stub"


def gen_activity_stubs(activities):
	activity_stubs = ""
	for activity in activities:
		activity_name = activity.attrib.get('{http://schemas.android.com/apk/res/android}name')
		#Cut the activity_name by the letter '.' and $
		delims = "[.\$]+"
		splits = filter(None, re.split(delims, activity_name))		
		
		if splits[1] == 'ActivityStub' and splits[2] == 'P08':
			declared_activity = splits[3]
			stub_class = form_class_name(declared_activity)
			activity_stubs += "\t\tpublic static class " + declared_activity + " extends " + stub_class + " {} \n"
	return activity_stubs


if __name__ == "__main__":
	container_app_file = sys.argv[1]
	gen_stubs(container_app_file)
