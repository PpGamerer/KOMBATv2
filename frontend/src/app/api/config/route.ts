import { NextResponse } from 'next/server';
import { promises as fs } from 'fs';
import path from 'path';

export async function GET() {
    const filePath = path.resolve(process.cwd(), '..', 'backend/config.txt'); // ออกจาก frontend แล้วหา config.txt ที่ root
    // console.log('Current working directory:', process.cwd());
    // console.log('File path:', filePath);

    try {
        const data = await fs.readFile(filePath, 'utf-8');
        // console.log('File content:', data);
        return NextResponse.json({ content: data });
    } catch (error) {
        // console.error('Error:', error);
        return NextResponse.json({ error: 'Failed to read config.txt' }, { status: 500 });
    }
}