import React, { PureComponent } from 'react';
import { PieChart, Pie, ResponsiveContainer } from 'recharts'; //Cell 지움
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

// PieChart 데이터
const data01 = [
  { name: 'Group A', value: 400 },
  { name: 'Group B', value: 300 },
  { name: 'Group C', value: 300 },
  { name: 'Group D', value: 200 },
];

const data02 = [
  { name: 'A1', value: 100 },
  { name: 'A2', value: 300 },
  { name: 'B1', value: 100 },
  { name: 'B2', value: 80 },
  { name: 'B3', value: 40 },
  { name: 'B4', value: 30 },
  { name: 'B5', value: 50 },
  { name: 'C1', value: 100 },
  { name: 'C2', value: 200 },
  { name: 'D1', value: 150 },
  { name: 'D2', value: 50 },
];

// LineChart 데이터
const lineChartData = [
  { name: 'Page A', uv: 4000, pv: 2400 },
  { name: 'Page B', uv: 3000, pv: 1398 },
  { name: 'Page C', uv: 2000, pv: 9800 },
  { name: 'Page D', uv: 2780, pv: 3908 },
  { name: 'Page E', uv: 1890, pv: 4800 },
  { name: 'Page F', uv: 2390, pv: 3800 },
  { name: 'Page G', uv: 3490, pv: 4300 },
];

export default class Example extends PureComponent {
  render() {
    return (
      <div 
    //   style={{ display: 'flex', justifyContent: 'space-around', height: '90%' }}
        className='flex justify-around h-[93%] pt-[10px]'
            >
        {/* LineChart */}
        <ResponsiveContainer width="60%" height="100%">
          <LineChart data={lineChartData} margin={{ top: 20, right: 30, left: 20, bottom: 1 }}
                    className='border-solid border-2 border-black'>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Legend align="left" verticalAlign="bottom" />
            <Line type="linear" dataKey="pv" stroke="#8a2be2" strokeWidth={3} />
            <Line type="linear" dataKey="uv" stroke="#32cd32" strokeWidth={3} />
          </LineChart>
        </ResponsiveContainer>

        {/* PieChart */}
        <ResponsiveContainer width="38%" height="100%" >
          <PieChart className='border-solid border-2 border-black'>
            <Pie data={data01} dataKey="value" cx="50%" cy="50%" outerRadius={60} fill="#8a2be2" label/>
            <Pie data={data02} dataKey="value" cx="50%" cy="50%" innerRadius={70} outerRadius={100} fill="#32cd32" label />
          </PieChart>
        </ResponsiveContainer>
      </div>
    );
  }
}
