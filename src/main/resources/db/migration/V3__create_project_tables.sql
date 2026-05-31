CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    release_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    zone VARCHAR(10) NOT NULL,
    cover_link VARCHAR(500)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_projects_name_release_date
    ON projects (LOWER(name), release_date);

CREATE INDEX IF NOT EXISTS idx_projects_name ON projects(name);
CREATE INDEX IF NOT EXISTS idx_projects_release_date ON projects(release_date);

CREATE TABLE IF NOT EXISTS project_artists (
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE RESTRICT,
    PRIMARY KEY (project_id, artist_id)
);

CREATE TABLE IF NOT EXISTS tracks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    track_number INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_tracks_project_track_number UNIQUE (project_id, track_number)
);

CREATE TABLE IF NOT EXISTS track_artists (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE RESTRICT,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uq_track_artists_track_artist UNIQUE (track_id, artist_id)
);

CREATE INDEX IF NOT EXISTS idx_tracks_project_id ON tracks(project_id);
CREATE INDEX IF NOT EXISTS idx_track_artists_track_id ON track_artists(track_id);
CREATE INDEX IF NOT EXISTS idx_track_artists_artist_id ON track_artists(artist_id);
