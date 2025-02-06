# coding: utf-8

"""
GraphScope Interactive API v0.3

This is the definition of GraphScope Interactive API, including   - AdminService API   - Vertex/Edge API   - QueryService   AdminService API (with tag AdminService) defines the API for GraphManagement, ProcedureManagement and Service Management.  Vertex/Edge API (with tag GraphService) defines the API for Vertex/Edge management, including creation/updating/delete/retrive.  QueryService API (with tag QueryService) defines the API for procedure_call, Ahodc query.

The version of the OpenAPI document: 1.0.0
Contact: graphscope@alibaba-inc.com
Generated by OpenAPI Generator (https://openapi-generator.tech)

Do not edit the class manually.
"""  # noqa: E501


from setuptools import find_packages  # noqa: H301
from setuptools import setup

# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools
NAME = "gs_interactive"
VERSION = "0.3"
PYTHON_REQUIRES = ">=3.7"
REQUIRES = [
    "urllib3 >= 1.25.3, < 2.1.0",
    "python-dateutil",
    "pydantic >= 2, <= 2.8.2",
    "typing-extensions >= 4.7.1",
    "neo4j >= 4.4.19",
    "gremlinpython >= 3.4.10",
    "protobuf >= 3.17.3",
]

import glob
import os
import subprocess
import sys
from distutils.cmd import Command


class BuildProto(Command):
    description = "build protobuf file"
    user_options = []

    def initialize_options(self):
        pass

    def finalize_options(self):
        pass

    def generate_proto(self, proto_path, output_dir, proto_files=None):
        if proto_files is None:
            proto_files = glob.glob(os.path.join(proto_path, "*.proto"))
        os.makedirs(output_dir, exist_ok=True)
        for proto_file in proto_files:
            if not os.path.exists(proto_file):
                proto_file = os.path.join(proto_path, proto_file)
            cmd = [
                sys.executable,
                "-m",
                "grpc_tools.protoc",
                "-I",
                proto_path,
                f"--python_out={output_dir}",
                f"--mypy_out={output_dir}",
                proto_file,
            ]
            subprocess.check_call(
                cmd,
                stderr=subprocess.STDOUT,
            )

    def run(self):
        self.generate_proto(
            "../../../../interactive_engine/executor/ir/proto/",
            "./gs_interactive/client/generated/",
        )
        self.generate_proto(
            "../../../../proto/error",
            "./gs_interactive/client/generated/",
            ["interactive.proto"],
        )


setup(
    name=NAME,
    version=VERSION,
    description="GraphScope Interactive API v0.3",
    author="OpenAPI Generator community",
    author_email="graphscope@alibaba-inc.com",
    url="",
    keywords=["OpenAPI", "OpenAPI-Generator", "GraphScope Interactive API v0.3"],
    install_requires=REQUIRES,
    packages=find_packages(exclude=["test", "tests"]),
    include_package_data=True,
    license="Apache 2.0",
    long_description_content_type="text/markdown",
    long_description="""\
    This is the definition of GraphScope Interactive API, including   - AdminService API   - Vertex/Edge API   - QueryService   AdminService API (with tag AdminService) defines the API for GraphManagement, ProcedureManagement and Service Management.  Vertex/Edge API (with tag GraphService) defines the API for Vertex/Edge management, including creation/updating/delete/retrive.  QueryService API (with tag QueryService) defines the API for procedure_call, Ahodc query.
    """,  # noqa: E501
    package_data={"gs_interactive": ["py.typed"]},
    cmdclass={"build_proto": BuildProto},
)
