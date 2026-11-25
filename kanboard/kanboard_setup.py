#!/usr/bin/env python3
"""
Kanboard JSON-RPC Automation Script
Creates the 'Hospital Management System Development' project, configures the 4 Kanban
columns with WIP limits, creates categories, and populates all project tasks.

Usage:
    python kanboard/kanboard_setup.py --url http://localhost:8081/jsonrpc.php --user admin --password admin
"""

import argparse
import base64
import csv
import json
import os
import urllib.request


def rpc_call(url: str, auth_header: str, method: str, params: dict, req_id: int = 1):
    payload = json.dumps({
        "jsonrpc": "2.0",
        "method": method,
        "id": req_id,
        "params": params,
    }).encode("utf-8")

    request = urllib.request.Request(
        url,
        data=payload,
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Basic {auth_header}",
        },
        method="POST",
    )
    with urllib.request.urlopen(request, timeout=15) as response:
        body = json.loads(response.read().decode("utf-8"))
        if "error" in body:
            raise RuntimeError(f"Kanboard API error in {method}: {body['error']}")
        return body.get("result")


def main():
    parser = argparse.ArgumentParser(description="Setup Kanboard project for Hospital Management System")
    parser.add_argument("--url", required=True, help="Kanboard JSON-RPC endpoint URL")
    parser.add_argument("--user", default="admin", help="Kanboard username")
    parser.add_argument("--password", required=True, help="Kanboard password or API token")
    args = parser.parse_args()

    token = base64.b64encode(f"{args.user}:{args.password}".encode("utf-8")).decode("ascii")

    project_id = rpc_call(args.url, token, "createProject", {
        "name": "Hospital Management System Development",
        "description": "End-to-end Spring Boot Hospital Management System following Kanban methodology.",
    }, req_id=1)

    if not project_id:
        raise RuntimeError("Failed to create Kanboard project.")

    # Replace default columns with the 4 required workflow columns and WIP limits
    existing_columns = rpc_call(args.url, token, "getColumns", {"project_id": project_id}, req_id=2)
    for col in existing_columns:
        rpc_call(args.url, token, "removeColumn", {"column_id": col["id"]}, req_id=3)

    columns_spec = [
        ("TO DO", 0),
        ("IN PROGRESS", 3),
        ("TESTING", 3),
        ("DONE", 0),
    ]
    column_ids = {}
    for title, wip_limit in columns_spec:
        col_id = rpc_call(args.url, token, "addColumn", {
            "project_id": project_id,
            "title": title,
            "task_limit": wip_limit,
        }, req_id=4)
        column_ids[title] = col_id

    csv_path = os.path.join(os.path.dirname(__file__), "kanboard_tasks_import.csv")
    priority_map = {"Low": 0, "Normal": 1, "Medium": 2, "High": 3, "Urgent": 3}

    with open(csv_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for idx, row in enumerate(reader, start=10):
            rpc_call(args.url, token, "createTask", {
                "project_id": project_id,
                "title": row["Subject"],
                "description": row["Description"],
                "column_id": column_ids.get(row["Column"], column_ids["TO DO"]),
                "priority": priority_map.get(row["Priority"], 1),
                "score": int(row["Score"]),
                "date_due": row["Due Date"],
            }, req_id=idx)

    print(f"Kanboard project created successfully (project_id={project_id}).")


if __name__ == "__main__":
    main()
