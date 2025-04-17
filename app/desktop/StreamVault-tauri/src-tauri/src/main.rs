// Prevents additional console window on Windows in release, DO NOT REMOVE!!
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use serde::{Deserialize, Serialize};
use tauri::command;

#[derive(Debug, Serialize, Deserialize)]
struct VideoResponse {
    #[serde(rename = "resCode")]
    res_code: String,
    message: String,
    record: Option<VideoRecord>,
}

#[derive(Debug, Serialize, Deserialize)]
struct VideoRecord {
    content: Vec<VideoItem>,
    #[serde(rename = "totalElements")]
    total_elements: i32,
}

#[derive(Debug, Serialize, Deserialize)]
struct VideoItem {
    videoname: String,
    videodesc: String,
    videocover: String,
    videounrealaddr: String,
    videoplatform: String,
    createtime: String,
}

#[command]
async fn fetch_videos(
    server_url: String,
    token: String,
    page_no: i32,
    page_size: i32,
    search_query: Option<String>,
) -> Result<VideoResponse, String> {
    let client = reqwest::Client::new();
    
    let mut form = vec![
        ("pageNo", page_no.to_string()),
        ("pageSize", page_size.to_string()),
    ];
    
    if let Some(query) = search_query {
        form.push(("videodesc", query.clone()));
        form.push(("videoname", query));
    }
    
    let response = client
        .post(format!("{}/api/findVideos?token={}", server_url, token))
        .form(&form)
        .send()
        .await
        .map_err(|e| e.to_string())?;
        
    let video_response = response
        .json::<VideoResponse>()
        .await
        .map_err(|e| e.to_string())?;
        
    Ok(video_response)
}

fn main() {
    tauri::Builder::default()
        .invoke_handler(tauri::generate_handler![fetch_videos])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
