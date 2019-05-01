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

	services = get_services(container_file_name)
	
	ServiceStub_file = container_file_name [:-19] + 'src/com/morgoo/droidplugin/stub/ServiceStub.java'

	new_service_stubs_str = ""

	new_service_stubs_str += "package com.morgoo.droidplugin.stub; \n"
	new_service_stubs_str += "public abstract class ServiceStub extends AbstractServiceStub { \n"
	new_service_stubs_str += gen_service_stubs(services)
	new_service_stubs_str += "}\n"

	f = open(ServiceStub_file, 'w')
	f.write(new_service_stubs_str)

#.stub.ServiceStub$StubP00$P00
def gen_service_stubs(services):
	service_stubs = ""
	for service in services:
		service_stub = ""
		service_name = service.attrib.get('{http://schemas.android.com/apk/res/android}name')
		delims = "[.\$]+"
		splits = filter(None, re.split(delims, service_name))
		if len(splits) < 4:
			continue

		stub_name = splits[2]
		
		service_stub += "\tpublic abstract static class " + stub_name + " extends ServiceStub { \n"
		service_stub += "\t\tpublic static class P00 extends " + stub_name + " {}\n"
		service_stub += "\t}\n"

		service_stubs += service_stub
	return service_stubs


if __name__ == "__main__":
	container_app_file = sys.argv[1]
	gen_stubs(container_app_file)
