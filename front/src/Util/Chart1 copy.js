import React, { PureComponent } from 'react';
import { PieChart, Pie, ResponsiveContainer } from 'recharts'; //Cell 지움
import { ComposedChart, Area, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

// PieChart 데이터
const data01 = [
  { name: '10월', value: 10 },
  { name: '11월', value: 10 },
  { name: '12월', value: 10 },
  { name: '1월', value: 10 },
  { name: '2월', value: 10 },
];

const data02 = [
  { name: '2월1일', value: 100 },
  { name: '2월2일', value: 300 },
  { name: '2월3일', value: 100 },
  { name: '2월4일', value: 80 },
  { name: '2월5일', value: 40 },
  { name: '2월6일', value: 30 },
  { name: '2월7일', value: 50 },
];

const chartData = [
  { name: '10월', 일별: 2, 월별: 13 },
  { name: '11월', 일별: 3, 월별: 14 },
  { name: '12월', 일별: 4, 월별: 15 },
  { name: '1월', 일별: 5, 월별: 16 },
  { name: '2월', 일별: 6, 월별: 17 }
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

          <ComposedChart 
          className='border-solid border border-black'
          width={500}
          height={400}
          data={chartData} margin={{ top: 10, right: 30, left: 10, bottom: 0 }}>
            <CartesianGrid strokeDasharray='3 3' />
            <XAxis dataKey='name' />
            <YAxis />
            <Tooltip />
            <Legend align='left' verticalAlign='bottom' />

            {/* Area Chart (배경 영역) */}
            <Area type='monotone' dataKey='일별' stroke='#8884d8' fill='#8884d8' fillOpacity={0.3} />

            {/* Line Chart (선 그래프) */}
            <Line type='monotone' dataKey='월별' stroke='#8a2be2' strokeWidth={3} />
          </ComposedChart>


        </ResponsiveContainer>

        {/* PieChart */}
        <ResponsiveContainer width="38%" height="100%" >
          <PieChart className='border-solid border border-black'>
            <Pie data={data01} dataKey="value" cx="50%" cy="50%" outerRadius={60} fill="#8a2be2" label />
            <Pie data={data02} dataKey="value" cx="50%" cy="50%" innerRadius={70} outerRadius={100} fill="#32cd32" label />
          </PieChart>
        </ResponsiveContainer>
      </div>
    );
  }
}
